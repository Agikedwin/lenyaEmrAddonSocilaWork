CREATE DEFINER=`root`@`%` PROCEDURE `create_coptic_test_pro_etl_tables`()
BEGIN
DECLARE script_id INT(11);
SET FOREIGN_KEY_CHECKS = 0;
CREATE DATABASE IF NOT EXISTS kenyaemr_etl DEFAULT CHARACTER SET utf8 COLLATE utf8_unicode_ci;


-- Log start time
INSERT INTO kenyaemr_etl.etl_script_status(script_name, start_time) VALUES('initial_create_coptic_etl_tables', NOW());
SET script_id = LAST_INSERT_ID();

DROP TABLE IF EXISTS kenyaemr_etl.etl_coptic_digital_xray;
-- create table kenyaemr_etl.etl_coptic_digital_xray--------
CREATE TABLE kenyaemr_etl.etl_coptic_digital_xray(
      uuid CHAR(38),
      client_name VARCHAR(150),
      encounter_id INT(11) NOT NULL PRIMARY KEY,
      visit_id INT(11) DEFAULT NULL,
      patient_id INT(11) NOT NULL ,
      location_id INT(11) DEFAULT NULL,
      encounter_provider_id INT(11),
      visit_date DATE,
      openmrs_id VARCHAR(150),
      date_created DATE,


      client_type VARCHAR(100),
      client_type_other VARCHAR(150),
      cad_score INT(11),
      cxr_results VARCHAR(100),
      abnormal_cxr_options VARCHAR(100),
      abnormal_cxr_options_other VARCHAR(200),
      cad_score_symptomatic VARCHAR(100),
      geneXpert_testing VARCHAR(150),
      sputum_AAFBs VARCHAR(100),
      biopsy VARCHAR(150),
      culture_sensitivity VARCHAR(150),
      diagnosed_with_TB VARCHAR(150),
      TB_treatment_started VARCHAR(150),
      comment VARCHAR(250),
      provider VARCHAR(150),
      voided INT(11),
      CONSTRAINT FOREIGN KEY (patient_id) REFERENCES kenyaemr_etl.etl_patient_demographics(patient_id),
      CONSTRAINT unique_uuid UNIQUE(uuid),
      INDEX(visit_date),
      INDEX(visit_id),
      INDEX(encounter_id),
      INDEX(patient_id),
      INDEX(patient_id, visit_date)
    );
  SELECT "Successfully created revised digital exray table";

END;