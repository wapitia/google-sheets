# google-sheets
Google Sheets Extensions | Scala module

Scala library extending Google's ```com.google.apis %% google-api-services-sheets```
by way of Google's authentication layer ```com.google.oauth-client %% google-oauth-client-jetty```

The app goal is to develop a financial and budgeting package in which a client 
enters here budget stream and financial goals as a series of Google sheets and
then have this app's service respond to client requests to deliver projected
budgets, charts, graphs, etc.
The financial stuff is work-in-progress, and once baked will likely be promoted
to another repository such as *bergen-budget*.

The first focus is the reading, processing and uploading of Google spreadsheets
(*Google Sheets*, abbreviated in this code as *GSheets*).

Also squirrelled away in here is the start of the rail-baron sandbox study,
which will be formalized and extracted sometime.

*Corey Morgan*
WapitiaSoft
July 2018
