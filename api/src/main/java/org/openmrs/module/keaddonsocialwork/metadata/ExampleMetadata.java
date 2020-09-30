/**
 * The contents of this file are subject to the OpenMRS Public License
 * Version 1.0 (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 * http://license.openmrs.org
 *
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied. See the
 * License for the specific language governing rights and limitations
 * under the License.
 *
 * Copyright (C) OpenMRS, LLC.  All Rights Reserved.
 */

package org.openmrs.module.keaddonexample.metadata;

import org.openmrs.module.metadatadeploy.bundle.AbstractMetadataBundle;
import org.springframework.stereotype.Component;

import static org.openmrs.module.metadatadeploy.bundle.CoreConstructors.*;

/**
 * Example metadata bundle
 */
@Component
public class ExampleMetadata extends AbstractMetadataBundle {

	public static class _EncounterType {
		// SOCIAL WORK
		public static final String SOCIALWORKADULT = "d69dedbd-3933-4e44-8292-bea939ce980a";

		public static final String SOCIALWORK = "56764aab-3727-46ec-96d2-18c652797609";

		//childsocialworkscreening.xml
		public static final String SOCIALWORKCHILD = "9e63e189-d4df-4cf4-81da-0b6ddf847cc2";

		//socialworklosttofollowup
		public static final String SOCIALWORKLOSTFOLLOWUP = "73b9fc00-d514-4edb-bd71-2d591413084b";


		//socialworkphonecontact
		public static final String SOCIALWORKPHONECONTACT = "26485b40-0308-44ad-8aa3-5c9368b18d8e";

		//socialworkverbalautopsy
		public static final String SOCIALWORKVERBALAUTOPSY = "ea6516d9-fb28-4b92-b1ae-6d4c72537147";

		// NUTRITION

		public static final String NUTRITIONSCREENING = "a173aa90-e0cd-43d6-aa4f-ea1a1ec35bdf";
		public static final String NUTRITIONFOLLOWUP = "15d7cc04-7a63-40a2-809c-0f45019dd5e2";

		//MEDICAL FOLLOW UP
		public static final String MEDICALFOLLOWUPVISUAL = "53ef0dfb-06db-4ad2-b31e-b022dd92cb5b";
		public static final String MEDICALFOLLOWUPNURSEFORM = "2fd2e6b6-c642-4918-9e3e-af16acb95116";

		// COUSELLING
		public static final String ADHERENCECOUNSELLING = "4e405cd3-76f5-4b00-9a23-8f5293abd6cd";
		public static final String CHILDCOUNSELLINGFOLLOWUP = "f9924d10-feae-4c55-8b36-7ecaaffc8abc";

		public static final String COUNSELLINGFOLLOWUP = "fc0cd769-96de-4826-ae6f-fc8d70499e9c";

		public static final String COUNSELLINGCHILD = "a06e1774-0ec1-4b53-bc97-10f267c7e82a";

		public static final String COUNSELLINADULT = "d879c4ee-0c3f-4c81-b504-66a117d0bd3e";

		public  static  final  String COUNSELLINGCHILDDISCLOSUREFU ="33e3123e-7d4d-485f-b1a4-30cf44c3b6c4";

		public  static  final  String COUNSELLINGCHILDDISCLOSRESESSION ="3ded0c86-c851-4af1-b71a-74c107c850ab";

		public  static  final  String COUNSELLINGADHRENCE1 ="ab7eff2a-fdab-484e-8a95-28522836c2a5";

		public  static  final  String COUNSELLINGGENERALSESSION ="f737cc35-9473-408a-901f-75a559af10a5";




	}

	public static class _Form {
		public static final String SOCIALWORKADULT = "b694b1bc-2086-47dd-a4ad-ba48f9471e4b";

		public static final String SOCIALWORK = "1b6ee7e0-5194-4896-b2bd-a37ed91b7f41";

		public static final String SOCIALWORKCHILD = "8635d6b6-d9d5-4bb9-a28d-cf3a90e1ba57";

		public static final String SOCIALWORKLOSTFOLLOWUP = "fd15765c-9086-4b63-beb8-3555c7837cd1";

		public static final String SOCIALWORKPHONECONTACT = "606219f3-de00-4113-9d44-9a84dff018ad";

		public static final String SOCIALWORKVERBALAUTOPSY = "6f65a8ae-03b2-44ad-8873-43e6be628c37";

		// NUTRITION

		public static final String NUTRITIONSCREENING = "daaa10c4-e971-4830-8e23-6c1c735e93da";

		public static final String NUTRITIONFOLLOWUP = "b6ba128d-78ad-425f-a491-73d9baa2a7b1";

		//MEDICAL FOLLOW UP
		public static final String MEDICALFOLLOWUPVISUAL = "f46f75f7-e38d-4fd3-b01e-7911ca58aec4";
		public static final String MEDICALFOLLOWUPNURSEFORM = "8df18cf0-3d49-4c2e-9f29-f7e650353b4e";

		// COUSELLING
		public static final String ADHERENCECOUNSELLING = "bf0235d1-5caa-4957-89a1-ae2690c7cd15";
		public static final String CHILDCOUNSELLINGFOLLOWUP = "ed966600-ff06-11ea-adc1-0242ac120002";

		public static final String COUNSELLINGFOLLOWUP = "050f7410-ad35-4a62-b509-2562da4b2ee4";

		public static final String COUNSELLINGCHILD = "6f9e453b-eaed-406e-8549-0d159c991f4f";

		public static final String COUNSELLINADULT = "12de1a31-2071-4402-9c84-43cbbb993227";

		public  static  final  String COUNSELLINGCHILDDISCLOSUREFU ="7c45570a-fe4b-4d12-a6af-c8bfcdfd7ded";

		public  static  final  String COUNSELLINGCHILDDISCLOSRESESSION ="74b77231-d53a-4726-ae2e-eed8b6dd6fc2";

		public  static  final  String COUNSELLINGADHRENCE1 ="c01ab09a-baf5-47c5-9b3f-02cdc27e9e8d";

		public  static  final  String COUNSELLINGGENERALSESSION ="751c10ae-0e47-40b1-b1f9-ccdac5a1fc84";


	}

	/**
	 * @see org.openmrs.module.metadatadeploy.bundle.AbstractMetadataBundle#install()
	 */
	@Override
	public void install() {
		// SOCIAL WORK
		install(encounterType("Adult Social Work encounter", "Just an example", _EncounterType.SOCIALWORKADULT));

		install(form("Adult Social Work Screening Form", null, _EncounterType.SOCIALWORKADULT, "1", _Form.SOCIALWORKADULT));


		install(encounterType("Social encounter", "Just an example", _EncounterType.SOCIALWORK));

		install(form("Social Work Form", null, _EncounterType.SOCIALWORK, "1", _Form.SOCIALWORK));

		install(encounterType("Child Social Work Screening encounter", "Child Social Work Screening", _EncounterType.SOCIALWORKCHILD));

		install(form("Child Social Work Screening Form", null, _EncounterType.SOCIALWORKCHILD, "1", _Form.SOCIALWORKCHILD));

		install(encounterType("Social Work Lost To Follow Up encounter", "Social Work Lost To Follow Up", _EncounterType.SOCIALWORKLOSTFOLLOWUP));

		install(form("Social Work Lost To Follow Up Form", null, _EncounterType.SOCIALWORKLOSTFOLLOWUP, "1", _Form.SOCIALWORKLOSTFOLLOWUP));

		install(encounterType("Patient Phone Contact Form encounter", "Patient Phone Contact Form", _EncounterType.SOCIALWORKPHONECONTACT));

		install(form("Patient Phone Contact Form ", null, _EncounterType.SOCIALWORKPHONECONTACT, "1", _Form.SOCIALWORKPHONECONTACT));

		install(encounterType("Verbal AutopsyForm encounter", "Verbal Autopsy Form", _EncounterType.SOCIALWORKVERBALAUTOPSY));

		install(form("Verbal Autopsy Form ", null, _EncounterType.SOCIALWORKVERBALAUTOPSY, "1", _Form.SOCIALWORKVERBALAUTOPSY));

		//NUTRITION
		install(encounterType("Nutrition encounter", "Just an Nutrition screening", _EncounterType.NUTRITIONSCREENING));

		install(form("Nutrition Screening  Form", null, _EncounterType.NUTRITIONSCREENING, "1", _Form.NUTRITIONSCREENING));

		install(encounterType("Nutrition Follow up encounter", "Just an Nutrition follow up", _EncounterType.NUTRITIONFOLLOWUP));

		install(form("Nutrition Follow up Form", null, _EncounterType.NUTRITIONFOLLOWUP, "1", _Form.NUTRITIONFOLLOWUP));

		//MEDICAL FOLLOW UP

		install(encounterType("Visual Inspection encounter", "Just an Nutrition follow up", _EncounterType.MEDICALFOLLOWUPVISUAL));

		install(form("Visual Inspection Form", null, _EncounterType.MEDICALFOLLOWUPVISUAL, "1", _Form.MEDICALFOLLOWUPVISUAL));

		install(encounterType("Nurses  Follow up encounter", "Nurses Form follow up", _EncounterType.MEDICALFOLLOWUPNURSEFORM));

		install(form("Nurses Form - Female patients only Form", null, _EncounterType.MEDICALFOLLOWUPNURSEFORM, "1", _Form.MEDICALFOLLOWUPNURSEFORM));

		//COUSELLING
		install(encounterType("Adherence Form encounter", "Just an Nutrition follow up", _EncounterType.ADHERENCECOUNSELLING));

		install(form("Adherence Form Form", null, _EncounterType.ADHERENCECOUNSELLING, "1", _Form.ADHERENCECOUNSELLING));

		install(encounterType("Child Counseling Follow Up encounter", "Child Counseling ", _EncounterType.CHILDCOUNSELLINGFOLLOWUP));

		install(form("Child Counseling Follow Up Form ", null, _EncounterType.CHILDCOUNSELLINGFOLLOWUP, "1", _Form.CHILDCOUNSELLINGFOLLOWUP));

		install(encounterType("Counseling Follow Up encounter", "Counseling Follow Up ", _EncounterType.COUNSELLINGFOLLOWUP));

		install(form("Counseling Follow Up ", null, _EncounterType.COUNSELLINGFOLLOWUP, "1", _Form.COUNSELLINGFOLLOWUP));

		install(encounterType("Counselor Screening Child encounter", "Counseling Follow Up ", _EncounterType.COUNSELLINGCHILD));

		install(form("Counselor Screening Child  ", null, _EncounterType.COUNSELLINGCHILD, "1", _Form.COUNSELLINGCHILD));

		install(encounterType("Counselor Screening Adult encounter", "Counselor Screening Adult ", _EncounterType.COUNSELLINADULT));




		install(encounterType("Child disclosure followup encounter", "Child disclosure followup ", _EncounterType.COUNSELLINGCHILDDISCLOSUREFU));
		install(form("Child disclosure followup encounter ", null, _EncounterType.COUNSELLINGCHILDDISCLOSUREFU, "1", _Form.COUNSELLINGCHILDDISCLOSUREFU));


		install(encounterType("Child disclosure session encounter", "Child disclosure session ", _EncounterType.COUNSELLINGCHILDDISCLOSRESESSION));

		install(form("Child disclosure session ", null, _EncounterType.COUNSELLINGCHILDDISCLOSRESESSION, "1", _Form.COUNSELLINGCHILDDISCLOSRESESSION));

		install(encounterType("Counseling adherence 1 encounter", "Counseling adherence 1", _EncounterType.COUNSELLINGADHRENCE1));

		install(form("Counseling adherence 1 ", null, _EncounterType.COUNSELLINGADHRENCE1, "1", _Form.COUNSELLINGADHRENCE1));

		install(encounterType("Couselling General Session encounter", "Couselling General Session ", _EncounterType.COUNSELLINGGENERALSESSION));

		install(form("Couselling General Session ", null, _EncounterType.COUNSELLINGGENERALSESSION, "1", _Form.COUNSELLINGGENERALSESSION));



	}
}