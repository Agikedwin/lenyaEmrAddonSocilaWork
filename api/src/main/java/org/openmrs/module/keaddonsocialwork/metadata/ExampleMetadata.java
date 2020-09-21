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
		public static final String EXAMPLE = "d69dedbd-3933-4e44-8292-bea939ce980a";

		public static final String SOCIALWORK = "56764aab-3727-46ec-96d2-18c652797609";

		//childsocialworkscreening.xml
		public static final String SOCIALWORKCHILD = "9e63e189-d4df-4cf4-81da-0b6ddf847cc2";

		//socialworklosttofollowup
		public static final String SOCIALWORKLOSTFOLLOWUP = "73b9fc00-d514-4edb-bd71-2d591413084b";


		//socialworkphonecontact
		public static final String SOCIALWORKPHONECONTACT = "26485b40-0308-44ad-8aa3-5c9368b18d8e";

		//socialworkverbalautopsy
		public static final String SOCIALWORKVERBALAUTOPSY = "ea6516d9-fb28-4b92-b1ae-6d4c72537147";



	}

	public static class _Form {
		public static final String EXAMPLE = "b694b1bc-2086-47dd-a4ad-ba48f9471e4b";

		public static final String SOCIALWORK = "1b6ee7e0-5194-4896-b2bd-a37ed91b7f41";

		public static final String SOCIALWORKCHILD = "8635d6b6-d9d5-4bb9-a28d-cf3a90e1ba57";

		public static final String SOCIALWORKLOSTFOLLOWUP = "fd15765c-9086-4b63-beb8-3555c7837cd1";

		public static final String SOCIALWORKPHONECONTACT = "606219f3-de00-4113-9d44-9a84dff018ad";

		public static final String SOCIALWORKVERBALAUTOPSY = "6f65a8ae-03b2-44ad-8873-43e6be628c37";

		//

	}

	/**
	 * @see org.openmrs.module.metadatadeploy.bundle.AbstractMetadataBundle#install()
	 */
	@Override
	public void install() {
		install(encounterType("Example encounter", "Just an example", _EncounterType.EXAMPLE));

		install(form("Adult Social Work Screening Form", null, _EncounterType.EXAMPLE, "1", _Form.EXAMPLE));


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


	}
}