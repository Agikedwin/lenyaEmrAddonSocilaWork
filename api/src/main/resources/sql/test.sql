
            CREATE DEFINER=`root`@`%` PROCEDURE `sp_update_etl_coptic_digital_xray`()
            BEGIN
                SELECT "Drop create etl coptic digital xray - Agik", CONCAT("Time: ", NOW());

                CALL `openmrs`.`create_coptic_etl_tables`();

                SELECT "Processing etl coptic digital xray - Agik", CONCAT("Time: ", NOW());

                INSERT INTO kenyaemr_etl.etl_coptic_digital_xray (
                    uuid, client_name, encounter_id, visit_id, patient_id, location_id, encounter_provider_id, visit_date,
                    openmrs_id, date_created, client_type, client_type_other, cad_score, cxr_results,
                    abnormal_cxr_options, abnormal_cxr_options_other, cad_score_symptomatic, geneXpert_testing,
                    sputum_AAFBs, biopsy, culture_sensitivity, diagnosed_with_TB, TB_treatment_started,
                    comment, provider, voided
                )
                SELECT
                    e.uuid AS uuid,
                    CONCAT_WS(" ", pn.given_name, pn.family_name) AS client_name,
                    e.encounter_id,
                    e.visit_id,
                    e.patient_id,
                    e.location_id,
                    pnn.person_id AS encounter_provider_id,
                    DATE(e.encounter_datetime),
                    MAX(IF(pit.uuid='dfacd928-0370-4315-99d7-6ec1c9f7ae76', pi.identifier, NULL)) AS openmrs_id,
                    NOW(),
                    MAX(IF(o.concept_id=164932, CASE o.value_coded
                        WHEN 160015 THEN "Newly diagnosed"
                        WHEN 164144 THEN "CD4 &lt; 200 cells/mm³"
                        WHEN 159833 THEN "Returning to care"
                        WHEN 159961 THEN "With TB symptoms"
                        WHEN 160542 THEN "TB presumed OPT"
                        WHEN 160536 THEN "TB Presumed IPD"
                        WHEN 160563 THEN "Transfer-In on TB treatment"
                        WHEN 165649 THEN "Symptomatic TB contacts"
                        WHEN 164931 THEN "Referral from other facility" ELSE "" END, NULL)) AS client_type,
                    MAX(IF(o.concept_id=165142, o.value_text, NULL)) AS client_type_other,
                    MAX(IF(o.concept_id=165416, o.value_numeric, NULL)) AS cad_score,
                    MAX(IF(o.concept_id=160108, CASE o.value_coded
                        WHEN 161297 THEN "Normal CXR"
                        WHEN 114100 THEN "Normal CXR but client symptomatic for TB"
                        WHEN 165649 THEN "Abnormal CXR" ELSE "" END, NULL)) AS cxr_results,
                    MAX(IF(o.concept_id=5219, CASE o.value_coded
                        WHEN 151 THEN "suggestive of TB"
                        WHEN 114100 THEN "Pneumonia"
                        WHEN 120749 THEN "Asthma"
                        WHEN 871 THEN "Pneumothorax"
                        WHEN 120345 THEN "Pulmonary Fibrosis"
                        WHEN 161297 THEN "Cardiomegaly"
                        WHEN 113054 THEN "Heart Failure"
                        WHEN 144576 THEN "Silicosis"
                        WHEN 106 THEN "Rib fracture"
                        WHEN 160737 THEN "Pleural Effusion"
                        WHEN 143129 THEN "Lung nodule"
                        WHEN 119537 THEN "Other(specify)" ELSE "" END, NULL)) AS abnormal_cxr_options,
                    MAX(IF(o.concept_id=160632, o.value_text, NULL)) AS abnormal_cxr_options_other,
                    MAX(IF(o.concept_id=5219, CASE o.value_coded
                        WHEN 105281 THEN "GeneXpert testing"
                        WHEN 74250 THEN "Sputum for AAFBs"
                        WHEN 1107 THEN "Biopsy"
                        WHEN 120345 THEN "Culture and sensitivity test" ELSE "" END, NULL)) AS cad_score_symptomatic,
                    MAX(IF(o.concept_id=161652, CASE o.value_coded
                        WHEN 159405 THEN "MTB Detected"
                        WHEN 159407 THEN "MTB not detected"
                        WHEN 160737 THEN "Not Done" ELSE "" END, NULL)) AS geneXpert_testing,
                    MAX(IF(o.concept_id=162229, CASE o.value_coded
                        WHEN 703 THEN "Positive"
                        WHEN 664 THEN "Negative"
                        WHEN 160737 THEN "Not Done" ELSE "" END, NULL)) AS sputum_AAFBs,
                    MAX(IF(o.concept_id=162737, CASE o.value_coded
                        WHEN 703 THEN "Positive"
                        WHEN 664 THEN "Negative"
                        WHEN 160737 THEN "Not Done" ELSE "" END, NULL)) AS biopsy,
                    MAX(IF(o.concept_id=162737, CASE o.value_coded
                        WHEN 703 THEN "Growth"
                        WHEN 664 THEN "No growth"
                        WHEN 160737 THEN "Not Done" ELSE "" END, NULL)) AS culture_sensitivity,
                    MAX(IF(o.concept_id=159394, CASE o.value_coded
                        WHEN 1065 THEN "Yes"
                        WHEN 1066 THEN "No" ELSE "" END, NULL)) AS diagnosed_with_TB,
                    MAX(IF(o.concept_id=163752, CASE o.value_coded
                        WHEN 1065 THEN "Yes"
                        WHEN 1066 THEN "No" ELSE "" END, NULL)) AS TB_treatment_started,
                    MAX(IF(o.concept_id=163042, o.value_text, NULL)) AS comment,
                    CONCAT_WS(" ", pnn.given_name, pnn.family_name) AS provider,
                    e.voided AS voided
                FROM encounter e
                INNER JOIN person p ON p.person_id = e.patient_id AND p.voided = 0
                INNER JOIN openmrs.person_name pn ON pn.person_id = e.patient_id AND p.voided = 0
                INNER JOIN openmrs.encounter_provider ep ON e.encounter_id = ep.encounter_id
                INNER JOIN openmrs.provider pv ON pv.provider_id = ep.provider_id
                INNER JOIN openmrs.person_name pnn ON pnn.person_id = pv.person_id
                INNER JOIN patient_identifier pi ON pi.patient_id = e.patient_id
                INNER JOIN patient_identifier_type pit ON pi.identifier_type = pit.patient_identifier_type_id
                INNER JOIN (
                    SELECT form_id
                    FROM form
                    WHERE uuid IN ('06d3291c-6bf4-4c1f-8429-5fca7ce004c8')
                ) f ON f.form_id = e.form_id
                LEFT OUTER JOIN obs o ON o.encounter_id = e.encounter_id
                    AND o.voided = 0
                    AND o.concept_id IN (
                        164932, 160108, 1000031, 161550, 165416, 168130, 5219, 160632,
                        1109, 161652, 162229, 162737, 159394, 163752, 163042
                    )
                GROUP BY e.patient_id, e.encounter_id;
            END;

