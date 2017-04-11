# Vaadin Charts from SAP HANA demo data

Simple Spring Boot with Vaadin application using Vaadin Charts. It's using (though not have to)
SAP HANA `SHINE` -demo database.

Made on top of excellent sample by Matthias Steiner:
https://github.com/SAP/cloud-spring-boot-sample

## Running locally

``mvn spring-boot:run -Drun.profiles=dev,standalone``

When ran locally, a in-memory db will be used. In this case the db initialized with
fraction of data from  `SHINE` -sample database. This just to get things going locally.

## Running in cloud

To create a `WAR` file that can be deployed to **SAP HANA Cloud Platform** (Tomcat 8 runtime) execute the following command:

``mvn -P neo clean package install``


## Prerequisites for using SAP Cloud

1. Create developer account [at sap cloudplatform page](https://cloudplatform.sap.com/try.html)
2. After successful registration, login
3. In SAP Cloud Platform Cockpit, create a new database:
   * Choose: Persistence/Databases & Schemas
   * Click new
   * Choose db system: HANA MDC, configure User for Shine true
   * Fill out rest to your liking (just remember those passwords :) )
   * Save
   * (Database creation takes quite some time, don't worry)
4. Once database is up, navigate to that db and from that view, click "SAP HANA Web-based Development Workbench"
5. If you're prompted for username-password use the ones you set in your shine -user
6. Choose "Catalog" (There should be a screen with options: Editor, Catalog, Security, Traces)
7. Expand Catalog and from under that you should have database (okay, not a db but catalog or schema or whatever) named as the user you made (default user was "shine", so by default the schema is "SHINE")
8. Open SQL -console and * copy demo sales-orders from shine-demo schema

### SQL for copying demo table

NOTE: if your shine user is something else than "shine", change the table prefix to match it

Create table
```SQL
CREATE COLUMN TABLE "SHINE"."sales_order"(
	"SALESORDERID" NVARCHAR(10) NOT NULL,
	"HISTORY.CREATEDBY.EMPLOYEEID" NVARCHAR(10),
	"HISTORY.CREATEDAT" DATE CS_DAYDATE,
	"HISTORY.CHANGEDBY.EMPLOYEEID" NVARCHAR(10),
	"HISTORY.CHANGEDAT" DATE CS_DAYDATE,
	"NOTEID" NVARCHAR(10),
	"PARTNER.PARTNERID" NVARCHAR(10),
	"CURRENCY" NVARCHAR(5),
	"GROSSAMOUNT" DECIMAL(15, 2) CS_FIXED,
	"NETAMOUNT" DECIMAL(15, 2) CS_FIXED,
	"TAXAMOUNT" DECIMAL(15, 2) CS_FIXED,
	"LIFECYCLESTATUS" NVARCHAR(1),
	"BILLINGSTATUS" NVARCHAR(1),
	"DELIVERYSTATUS" NVARCHAR(1),
	PRIMARY KEY (
		"SALESORDERID"
	)
);
```

Copy data from demo
```SQL
INSERT INTO "SHINE"."sales_order" (
	"SALESORDERID",
	"HISTORY.CREATEDBY.EMPLOYEEID",
	"HISTORY.CREATEDAT",
	"HISTORY.CHANGEDBY.EMPLOYEEID",
	"HISTORY.CHANGEDAT",
	"NOTEID",
	"PARTNER.PARTNERID",
	"CURRENCY",
	"GROSSAMOUNT",
	"NETAMOUNT",
	"TAXAMOUNT",
	"LIFECYCLESTATUS",
	"BILLINGSTATUS",
	"DELIVERYSTATUS"
)
SELECT
	"SALESORDERID",
	"HISTORY.CREATEDBY.EMPLOYEEID",
	"HISTORY.CREATEDAT",
	"HISTORY.CHANGEDBY.EMPLOYEEID",
	"HISTORY.CHANGEDAT",
	"NOTEID",
	"PARTNER.PARTNERID",
	"CURRENCY",
	"GROSSAMOUNT",
	"NETAMOUNT",
	"TAXAMOUNT",
	"LIFECYCLESTATUS",
	"BILLINGSTATUS",
	"DELIVERYSTATUS"
FROM "SAP_HANA_DEMO"."sap.hana.democontent.epm.data::SO.Header";
```


## Deploying to cloud (via webpage)

In SAP Cloud Platform Cockpit:

First time:

1. Choose Applications / Java Applications / Deploy Application
  1. Choose war-file (in target folder, named sap-hana-vaadin-chart.war)
  2. Run-time Name: Java Web Tomcat 8
  3. JVM Version: JRE 8
  4. JVM Arguments: -Dspring.profiles.active=neo
  5. Deploy (But do not start the application)
2. Choose you database
3. Choose "Data Source Bindings"
4. Fill in username and password and save
5. Start your application

If you're only updating (meaning you've done the first time already), then just update your applications by giving a new war-file and restart application.

## Using
When launched, the application read all years available in database. With that info it fills the combobox.
When year is chosen (first will be chosen by default) the chart will be re-drawn. 

![Screenshot](https://cloud.githubusercontent.com/assets/19262966/24908100/796b7298-1ec7-11e7-864c-2012007e0335.jpg)

## Using Charts

Vaadin Charts is a commercial tool, consult
https://vaadin.com/charts
To get a license


