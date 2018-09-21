package com.wapitia
package gsheets

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.security.GeneralSecurityException;

import com.google.api.client.auth.oauth2.Credential
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport
import com.google.api.client.http.javanet.NetHttpTransport
import com.google.api.client.json.jackson2.JacksonFactory
import com.google.api.client.util.store.FileDataStoreFactory
import com.google.api.services.sheets.v4.Sheets
import com.google.api.services.sheets.v4.SheetsScopes
import com.google.api.services.sheets.v4.model.ValueRange
import com.google.api.client.auth.oauth2.AuthorizationCodeFlow

import com.wapitia.spreadsheet.marshal.SheetRow

/** Load spreadsheet data from Google Drive via the google API services.
 */
class GoogleSheetsAccess(appName: String, scopes: List[String]) {

  import GoogleSheetsAccess._

  /** Load the values from a Google Sheet given the sheet's SID and a parsable
   *  range string to load as a 2x2 grid of AnyRefs.
   *  Uses default client secrets file and credentials folder.
   *  Delivers string values as seen in the heads-up screen formats.
   */
  def loadSheetFormattedValues(sheetSid: String, loadRange: String) : List[SheetRow] =
    loadSheet(sheetSid, loadRange, ValueRenderOption.FORMATTED_VALUE, ClientSecretsFile, CredentialsFolder)

  /** Load the values from a Google Sheet given the sheet's SID and a parseable
   *  range string to load as a 2x2 grid of AnyRefs.
   *  Uses default client secrets file and credentials folder.
   *  o dates as BigDecimal values which are days since 1899-12-30
   *  o numbers as BigDecimal unformatted values. This includes currency values.
   *  o other text as String values pretty much unchanged
   *  o Null object on empty cells
   */
  def loadSheetUnformattedValues(sheetSid: String, loadRange: String) : List[SheetRow] =
    loadSheet(sheetSid, loadRange, ValueRenderOption.UNFORMATTED_VALUE, ClientSecretsFile, CredentialsFolder)

  /** Load the values from a Google Sheet given the sheet's SID and a parseable
   *  range string to load as a 2x2 grid of AnyRefs.
   *  Uses default client secrets file and credentials folder.
   *  This delivers the uncalculated formulas as strings starting with "="
   *  instead of the raw values where formulas are defined, but otherwise delivers data as
   *  `loadSheetUnformattedValues`.
   */
  def loadSheetFormula(sheetSid: String, loadRange: String) : List[SheetRow] =
    loadSheet(sheetSid, loadRange, ValueRenderOption.FORMULA, ClientSecretsFile, CredentialsFolder)

  /** Load the values from a Google Sheet given the sheet's SID and a parseable
   *  range string to load as a 2x2 grid of AnyRefs.
   *
   *  @param sheetSid    is the long unique string identifying the spreadsheet on docs.google.com,
   *                     of the form "19MQxwFoobarQId_wK6vPJmb9oZRHjqyLMNOPQRS"
   *  @param loadRange   google-specific spreadsheet sheet, row column range specification
   *                     of the form "Sheet Name!A1:P35" or "Sheet Name!A:P" for all rows.
   *  @param valueOption the value render option as expected by Google sheets, one of:
   *                     o FORMATTED_VALUE String values given as seen on the page, such as "$1,233.98"
   *                     o UNFORMATTED_VALUE Unformatted values, BigDecimals for numbers and dates, Strings for others.
   *                     o FORMULA Formula cells are returned as Strings beginning with "="
   *  @param clientSecretsFile Name of the pre-established client secrets file, relative to
   *                     the resources class path and package "com/wapitia/sheets/"
   *  @param credentialsFolder the folder where the pre-established credentials are squirreled.
   */
  def loadSheet(
      sheetSid: String,
      loadRange: String,
      valueOption: ValueRenderOption,
      clientSecretsFile: String,
      credentialsFolder: String): List[SheetRow] = {

    import scala.collection.JavaConverters._
    import GoogleSheetsAccess._

    try {
      val httpTransport: NetHttpTransport  = GoogleNetHttpTransport.newTrustedTransport()

      val cred : Credential = getCredentials(httpTransport, clientSecretsFile, credentialsFolder);
      val service: Sheets = new Sheets.Builder(httpTransport, GoogleSheetsAccess.JsonFactory, cred)
        .setApplicationName(appName)
        .build()

      val response : ValueRange = service
        .spreadsheets
        .values
        .get(sheetSid, loadRange)
        .setValueRenderOption(valueOption.name)
        .execute()

      response.getValues.asScala.map(_.asScala.toList).toList
    } catch {
      case gse: GeneralSecurityException => throw new GSheetsSecurityException(gse)
      case ioe: IOException => throw new GSheetsIOException(ioe)
    }
  }

  /**
   * Creates an authorized Credential object.
   *
   * @param HTTP_TRANSPORT
   *            The network HTTP Transport.
   * @return An authorized Credential object.
   * @throws IOException
   *             If there is no client_secret.
   */
  def getCredentials(httpTransport: NetHttpTransport, clientSecretsFile: String, credentialsFolder: String): Credential = {

    import scala.collection.JavaConverters._

    // Load client secrets.
    val clientSecrets: GoogleClientSecrets = loadClientSecrets(clientSecretsFile)

    // Build flow and trigger user authorization request.
    val fileDataStoreFactory: FileDataStoreFactory = new FileDataStoreFactory(new java.io.File(credentialsFolder))
    val flow: AuthorizationCodeFlow = new GoogleAuthorizationCodeFlow
      .Builder(httpTransport, GoogleSheetsAccess.JsonFactory, clientSecrets, scopes.asJava)
      .setDataStoreFactory(fileDataStoreFactory)
      .setAccessType(OfflineAccess)
      .build()

    new AuthorizationCodeInstalledApp(flow, new LocalServerReceiver()).authorize(GoogleSheetsAccess.AuthUser)
  }

  /** Load Client secrets via some JSON file generated previously from google oauth job.
   *  The file is searched for in resources according to the protocol given by
   *  in [[Class.getResourceAsStream(String)]] and relative to this `GoogleSheetsAccess` package,
   *  namely "com/wapitia/gsheets/".
   *  Alternatively, if the file begins with a slash, "/", indicating an absolute file,
   *  then the file is searched for from the base of resources in the class path.
   *
   *  @return the [[GoogleClientSecrets]] object from the given file in resources.
   *
   *  @throws GSheetsException if the dedicated named secrets file does not exist in resources.
   *  @throws GSheetsException if the secrets file is found, but empty.
   *  @throws GsheetsIOException if the secrets file is unavailable or unreadable.
   */
  def loadClientSecrets(clientSecretsFile: String): GoogleClientSecrets = {

    val accessClass: Class[_ <: GoogleSheetsAccess.type] = GoogleSheetsAccess.getClass

    // for error descriptions, when necessary
    def pkgMsg: String = packageMessage(clientSecretsFile, accessClass)

    val in: InputStream = accessClass.getResourceAsStream(clientSecretsFile)
    if (in == null)
      throw new GSheetsException(s"Client Secrets file is not found: $pkgMsg");

    val avail = try {
      if (in.available() == 0)
        throw new GSheetsException(s"Client Secrets file is empty: $pkgMsg");
    }
    catch {
      case ioe: IOException => throw new GSheetsIOException(s"Client Secrets file is unavailable: $pkgMsg", ioe);
    }
    val inputStreamReader: InputStreamReader = new InputStreamReader(in)
    val clientSecrets: GoogleClientSecrets = try {
      GoogleClientSecrets.load(GoogleSheetsAccess.JsonFactory, inputStreamReader)
    }
    catch {
      case ioe: IOException => throw new GSheetsIOException(s"Client Secrets file is unreadable: $pkgMsg", ioe);
    }
    clientSecrets
  }

  def packageMessage(filename: String, accessClass: Class[_]) =
    if (filename.startsWith("/"))
      s"""file named "$filename" in resources."""
    else {
      val packageName = accessClass.getPackage.getName
      s"""file named "$filename" in resources at package "$packageName"."""
    }
}

object GoogleSheetsAccess {

  def apply(appName: String, scopes: List[String]): GoogleSheetsAccess =
    new GoogleSheetsAccess(appName, scopes)

  def readOnlyAccess(appName: String): GoogleSheetsAccess =
    new GoogleSheetsAccess(appName, ReadOnlyScopes)

  /**
   * Global instance of the scopes required by this quickstart. If modifying these
   * scopes, delete your previously saved credentials/ folder.
   */
  val ReadOnlyScopes: List[String] = List(SheetsScopes.SPREADSHEETS_READONLY)
  val ReadOnlyScope: String = SheetsScopes.SPREADSHEETS_READONLY
  val OfflineAccess = "offline"
  val AuthUser = "user"

  val ClientSecretsFile = "client_secret.json"
  val JsonFactory: com.google.api.client.json.JsonFactory = JacksonFactory.getDefaultInstance()

  // folder is a directory in a file system for which this app has
  // read/write access.
  val CredentialsFolder = "credentials"
}
