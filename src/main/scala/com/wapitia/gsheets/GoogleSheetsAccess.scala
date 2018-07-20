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
//import com.google.api.client.json.JsonFactory
import com.google.api.client.json.jackson2.JacksonFactory
import com.google.api.client.util.store.FileDataStoreFactory
import com.google.api.services.sheets.v4.Sheets
import com.google.api.services.sheets.v4.SheetsScopes
import com.google.api.services.sheets.v4.model.ValueRange
import com.google.api.client.auth.oauth2.AuthorizationCodeFlow

/** Load spreadsheet data from Google Drive via the google API services.
 */
class GoogleSheetsAccess(appName: String, scopes: List[String]) {
  
  import GoogleSheetsAccess._

  def loadSheetFormattedValues(sheetSid: String, loadRange: String) : List[List[AnyRef]] = 
    loadSheet(sheetSid, loadRange, ValueRenderOption.FORMATTED_VALUE)
  
  /** Load the values from a Google Sheet given the sheet's SID and a parseable
   *  range string to load as a 2x2 grid of AnyRefs.
   *  o dates as BigDecimal values which are days since 1899-12-30
   *  o numbers as BigDecimal unformatted values. This includes currency values.
   *  o other text as String values pretty much unchanged
   *  o Null object on empty cells
   */
  def loadSheetUnformattedValues(sheetSid: String, loadRange: String) : List[List[AnyRef]] = 
    loadSheet(sheetSid, loadRange, ValueRenderOption.UNFORMATTED_VALUE)
  
  def loadSheetFormula(sheetSid: String, loadRange: String) : List[List[AnyRef]] = 
    loadSheet(sheetSid, loadRange, ValueRenderOption.FORMULA)
  
  /** Load the values from a Google Sheet given the sheet's SID and a parseable
   *  range string to load as a 2x2 grid of AnyRefs.
   *  The ValueRenderOption specifies the mode of the values to return:
   *  o FORMATTED_VALUE String values given as seen on the page, such as "$1,233.98"
   *  o UNFORMATTED_VALUE Unformatted values, BigDecimals for numbers and dates, Strings for others.
   *  o FORMULA Formula cells are returned as Strings beginning with "=" 
   */
	def loadSheet(sheetSid: String, loadRange: String, valueOption: ValueRenderOption) : List[List[AnyRef]] = {
		
	  import scala.collection.JavaConverters._
	  import GoogleSheetsAccess._
	  
		try {
			val httpTransport: NetHttpTransport  = GoogleNetHttpTransport.newTrustedTransport()
			
			val cred : Credential = getCredentials(httpTransport);
			val service: Sheets = new Sheets.Builder(httpTransport, GoogleSheetsAccess.JsonFactory, cred)
				.setApplicationName(appName)
				.build()
			
			val response : ValueRange = service
			  .spreadsheets()
			  .values()
			  .get(sheetSid, loadRange)
			  .setValueRenderOption(valueOption.name)
			  .execute()
			  
			response.getValues().asScala.map(_.asScala.toList).toList
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
	def getCredentials(httpTransport: NetHttpTransport): Credential = {
	  
	  import scala.collection.JavaConverters._

		// Load client secrets.
		val clientSecrets: GoogleClientSecrets = loadClientSecrets

		// Build flow and trigger user authorization request.
		val fileDataStoreFactory: FileDataStoreFactory = new FileDataStoreFactory(new java.io.File(CredentialsFolder))
		val flow: AuthorizationCodeFlow = new GoogleAuthorizationCodeFlow
		  .Builder(httpTransport, GoogleSheetsAccess.JsonFactory, clientSecrets, scopes.asJava)
			.setDataStoreFactory(fileDataStoreFactory)
			.setAccessType(OfflineAccess)
			.build()
			
		new AuthorizationCodeInstalledApp(flow, new LocalServerReceiver()).authorize(GoogleSheetsAccess.AuthUser)
	}

	def loadClientSecrets: GoogleClientSecrets = {
	  
	  import GoogleSheetsAccess._
	  
		def in: InputStream = GoogleSheetsAccess.getClass.getResourceAsStream(ClientSecretsFile)
		def inputStreamReader: InputStreamReader = new InputStreamReader(in)
		def clientSecrets: GoogleClientSecrets = GoogleClientSecrets.load(GoogleSheetsAccess.JsonFactory, inputStreamReader)
		clientSecrets
	}

	
}

object GoogleSheetsAccess {
  
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
	val CredentialsFolder = "credentials"
}
