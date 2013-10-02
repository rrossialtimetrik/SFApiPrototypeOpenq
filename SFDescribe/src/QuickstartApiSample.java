import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;

import com.sforce.soap.partner.DescribeGlobalResult;
import com.sforce.soap.partner.DescribeLayout;
import com.sforce.soap.partner.DescribeLayoutComponent;
import com.sforce.soap.partner.DescribeLayoutItem;
import com.sforce.soap.partner.DescribeLayoutResult;
import com.sforce.soap.partner.DescribeLayoutRow;
import com.sforce.soap.partner.DescribeLayoutSection;
import com.sforce.soap.partner.DescribeSObjectResult;
import com.sforce.soap.partner.Field;
import com.sforce.soap.partner.FieldType;
import com.sforce.soap.partner.GetUserInfoResult;
import com.sforce.soap.partner.PartnerConnection;
import com.sforce.soap.partner.PicklistEntry;
import com.sforce.soap.partner.QueryResult;
import com.sforce.soap.partner.sobject.SObject;
import com.sforce.ws.ConnectionException;
import com.sforce.ws.ConnectorConfig;

public class QuickstartApiSample {
	PartnerConnection connection;
	static String authEndPoint = "https://login.salesforce.com/services/Soap/u/28.0";
	
	// TODO Fill this variables with Salesforce user and Salesforce password. If the org that you are trying to connect have the security token enable, you must set the var password=Salesforce Password+""+Org SecurityToken
	String user="";
	String password="";
	public static void main(String[] args) {
		QuickstartApiSample sample = new QuickstartApiSample(authEndPoint);
		sample.run();
	}

	public void run() {
		// Make a login call
		if (login()) {

			System.out.println("This are the list of Contact Fields and its propierties");
			describeSObjectsSample();

			System.out.println("This are the propierties of the Contact- KOL Layout ");
			describeLayout(getRecordTypeID());
			logout();
		}
	}
	/**
	 * 
	 * @return the id of the RecordType related to Contact  KOL Layout 
	 */
	private String getRecordTypeID() {
		String soqlQuery = "SELECT id FROM RecordType where Name='KOL' AND SObjectType='Contact' limit 1";
		QueryResult qr;
		try {
			qr = connection.query(soqlQuery);
			if (qr.getSize() > 0) {
				SObject[] records = qr.getRecords();
				return records[0].getId();
			}
		} catch (ConnectionException e) {
			e.printStackTrace();
		}
		return null;
	}

	private void describeLayout(String id) {
		try {

			DescribeLayoutResult dlr = connection.describeLayout("Contact",
					new String[] { id });
			System.out.println("");
			System.out.println("<START>"); 
			ArrayList<DescribeLayout> dl = new ArrayList<DescribeLayout>();
			for (DescribeLayout d : dlr.getLayouts()) {
				for (DescribeLayoutSection dls : d.getEditLayoutSections()) {
					System.out.println("Heading: " + dls.getHeading() + " "
							+ dls.getUseHeading() + " " + dls.isUseHeading()
							+ ">>");
					for (DescribeLayoutRow desclr : dls.getLayoutRows()) {
						System.out.println("NumOfItems: "
								+ desclr.getNumItems() + " ");
						for (DescribeLayoutItem dli : desclr.getLayoutItems()) {
							System.out.println("Items Prop:  Label: "
									+ dli.getLabel() + " Required: "
									+ dli.isRequired() + " Editable: "
									+ dli.getEditable() + "  Placeholder: "
									+ dli.getPlaceholder() + "");
							for (DescribeLayoutComponent dlc : dli
									.getLayoutComponents()) {
								System.out.println("Components: DisplayLine: "
										+ dlc.getDisplayLines() + " TabOrd: "
										+ dlc.getTabOrder() + " Value: "
										+ dlc.getValue() + " Type: "
										+ dlc.getType());

							}

						}	
						System.out.println("");
					}
					System.out.println("<<<<<<<>>>>>>>");
				}
				System.out.println("");
				System.out.println("<END>");
				System.out.println("");
			}
		} catch (ConnectionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	// Constructor
	public QuickstartApiSample(String authEndPoint) {
		this.authEndPoint = authEndPoint;
	}

	private boolean login() {
		boolean success = false;
		String username = user;// getUserInput("Enter username: ");
		String password = this.password;// getUserInput("Enter password: ");

		try {
			ConnectorConfig config = new ConnectorConfig();
			config.setUsername(username);
			config.setPassword(password);

			System.out.println("AuthEndPoint: " + authEndPoint);
			config.setAuthEndpoint(authEndPoint);

			connection = new PartnerConnection(config);
			printUserInfo(config);

			success = true;
		} catch (ConnectionException ce) {
			ce.printStackTrace();
		}

		return success;
	}

	private void printUserInfo(ConnectorConfig config) {
		try {
			GetUserInfoResult userInfo = connection.getUserInfo();

			System.out.println("\nLogging in ...\n");
			System.out.println("UserID: " + userInfo.getUserId());
			System.out.println("User Full Name: " + userInfo.getUserFullName());
			System.out.println("User Email: " + userInfo.getUserEmail());
			System.out.println();
			System.out.println("SessionID: " + config.getSessionId());
			System.out.println("Auth End Point: " + config.getAuthEndpoint());
			System.out.println("Service End Point: "
					+ config.getServiceEndpoint());
			System.out.println();
		} catch (ConnectionException ce) {
			ce.printStackTrace();
		}
	}

	private void logout() {
		try {
			connection.logout();
			System.out.println("Logged out.");
		} catch (ConnectionException ce) {
			ce.printStackTrace();
		}
	}


	/**
	 * The following method illustrates the type of metadata information that
	 * can be obtained for each object available to the user. The sample client
	 * application executes a describeSObject call on a given object and then
	 * echoes the returned metadata information to the console. Object metadata
	 * information includes permissions, field types and length and available
	 * values for picklist fields and types for referenceTo fields.
	 * This mehtod was simplified to show only the fields propierties
	 */
	private void describeSObjectsSample() {
		String objectToDescribe = "Contact";
		try {
			// Call describeSObjects() passing in an array with one object type
			DescribeSObjectResult[] dsrArray = connection.describeSObjects(new String[] { objectToDescribe });

			// Since we described only one sObject, we should have only
			// one element in the DescribeSObjectResult array.
			DescribeSObjectResult dsr = dsrArray[0];
			// Now, retrieve metadata for each field
			for (int i = 0; i < dsr.getFields().length; i++) {
				// Get the field
				Field field = dsr.getFields()[i];

				// Write some field properties
				System.out.println("Field name: " + field.getName());
				System.out.println("\tField Label: " + field.getLabel());

				// This next property indicates that this
				// field is searched when using
				// the name search group in SOSL
				if (field.getNameField())
					System.out.println("\tThis is a name field.");

				if (field.getRestrictedPicklist())
					System.out.println("This is a RESTRICTED picklist field.");

				System.out.println("\tType is: " + field.getType());

				if (field.getLength() > 0)
					System.out.println("\tLength: " + field.getLength());

				if (field.getScale() > 0)
					System.out.println("\tScale: " + field.getScale());

				if (field.getPrecision() > 0)
					System.out.println("\tPrecision: " + field.getPrecision());

				if (field.getDigits() > 0)
					System.out.println("\tDigits: " + field.getDigits());

				if (field.getCustom())
					System.out.println("\tThis is a custom field.");

				// Write the permissions of this field
				if (field.getNillable())
					System.out.println("\tCan be nulled.");
				if (field.getCreateable())
					System.out.println("\tCreateable");
				if (field.getFilterable())
					System.out.println("\tFilterable");
				if (field.getUpdateable())
					System.out.println("\tUpdateable");

				// If this is a picklist field, show the picklist values
				if (field.getType().equals(FieldType.picklist)) {
					System.out.println("\t\tPicklist values: ");
					PicklistEntry[] picklistValues = field.getPicklistValues();

					for (int j = 0; j < field.getPicklistValues().length; j++) {
						System.out.println("\t\tValue: "
								+ picklistValues[j].getValue());
					}
				}

				// If this is a foreign key field (reference),
				// show the values
				if (field.getType().equals(FieldType.reference)) {
					System.out.println("\tCan reference these objects:");
					for (int j = 0; j < field.getReferenceTo().length; j++) {
						System.out.println("\t\t" + field.getReferenceTo()[j]);
					}
				}
				System.out.println("");
			}
		} catch (ConnectionException ce) {
			ce.printStackTrace();
		}
	}

}