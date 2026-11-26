package org.openmrs.module.keaddonsocialwork.reporting.builder;

import org.openmrs.module.kenyacore.report.ReportDescriptor;
import org.openmrs.module.kenyacore.report.ReportUtils;
import org.openmrs.module.kenyacore.report.builder.AbstractReportBuilder;
import org.openmrs.module.kenyacore.report.builder.Builds;
import org.openmrs.module.reporting.dataset.definition.DataSetDefinition;
import org.openmrs.module.reporting.dataset.definition.SqlDataSetDefinition;
import org.openmrs.module.reporting.evaluation.parameter.Mapped;
import org.openmrs.module.reporting.evaluation.parameter.Parameter;
import org.openmrs.module.reporting.report.definition.ReportDefinition;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

@Component
@Builds({"kenyaemr.coptic.module.report.sgbv" })

public class SGBVReportBuilder extends  AbstractReportBuilder {

    public static final String DATE_FORMAT = "dd/MM/yyyy";

    @Override
    protected List<Mapped<DataSetDefinition>> buildDataSets(ReportDescriptor descriptor, ReportDefinition report) {

        return Arrays.asList(ReportUtils.map(reportColumns(), "startDate=${startDate},endDate=${endDate}"));
    }

    @Override
    protected List<Parameter> getParameters(ReportDescriptor reportDescriptor) {
        return Arrays.asList(new Parameter("startDate", "Start Date", Date.class), new Parameter("endDate", "End Date",
                Date.class), new Parameter("dateBasedReporting", "", String.class));
    }

    /**
     * Columns for the report
     *
     * @return
     */
    protected DataSetDefinition reportColumns() {
        SqlDataSetDefinition dsd = new SqlDataSetDefinition();
        dsd.setName("pvcspvc");
        dsd.setDescription("PVC AND SPVC Line List");
        dsd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        dsd.addParameter(new Parameter("endDate", "End Date", Date.class));

        dsd.setSqlQuery("SELECT \n" +
                " ANY_VALUE(c.identifier) AS `Patient Identifier`,\n" +
                "                        ANY_VALUE(CAST(`openmrs`.`r`.`encounter_datetime` AS DATE)) AS `Visit Date`, \n" +
                "                        ANY_VALUE(CAST(`openmrs`.`r`.`date_created` AS DATE)) AS `Last Updated`,\n" +
                "                        ANY_VALUE(`openmrs`.`q`.`name`) AS `Form Name`,\n" +
                "                        ANY_VALUE((SELECT\n" +
                "                                    `openmrs`.`users`.`username`\n" +
                "                                FROM\n" +
                "                                    `openmrs`.`users`\n" +
                "                                WHERE\n" +
                "                                    (`openmrs`.`users`.`user_id` = `openmrs`.`v`.`creator`))) AS `Recorded By`,\n" +
                "                        IF((MAX(`openmrs`.`r`.`date_created`) > MIN(`openmrs`.`r`.`date_created`)),\n" +
                "                                MAX(`openmrs`.`r`.`date_created`),\n" +
                "                                NULL) AS `Last Modified Date`,\n" +
                "                        MAX(\n" +
                "                        CASE\n" +
                "                            WHEN v.concept_id IN (5096)\n" +
                "                            THEN v.value_datetime\n" +
                "                            ELSE NULL\n" +
                "                        END\n" +
                "                    ) AS `Incident Date`,\n" +
                "                        CASE\n" +
                "                             -- SPVC Follow up takes precedence over a recorded Physical classification.\n" +
                "                             WHEN MAX(IF(TRIM(`openmrs`.`q`.`name`) = 'SPVC Follow up', 1, 0)) = 1\n" +
                "                                 THEN 'Sexual'\n" +
                "                             ELSE NULLIF(\n" +
                "                                 MAX(IF(`openmrs`.`v`.`concept_id` = 165205,\n" +
                "                                     CASE `openmrs`.`v`.`value_coded`\n" +
                "                                         WHEN 152370 THEN 'Sexual'\n" +
                "                                         WHEN 118668 THEN 'Emmotional'\n" +
                "                                         WHEN 152292 THEN 'Physical'\n" +
                "                                         ELSE ''\n" +
                "                                     END,\n" +
                "                                     NULL)),\n" +
                "                                 ''\n" +
                "                             )\n" +
                "                         END AS `Type of Violence`,\n" +
                "                        /* Sexual subtype must be populated only for sexual-violence encounters.\n" +
                "                       152292 under 165205 = PHYSICAL; under 167255 = SEXUAL ASSAULT.\n" +
                "                       An explicit Physical/Emotional classification takes precedence over a\n" +
                "                       form-name fallback, preventing physical cases from being misclassified. */\n" +
                "                      CASE\n" +
                "                          WHEN MAX(IF(v.concept_id = 165205\n" +
                "                                      AND v.value_coded IN (152292, 118668), 1, 0)) = 1\n" +
                "                              THEN NULL\n" +
                "                          WHEN MAX(IF(v.concept_id = 165205\n" +
                "                                      AND v.value_coded = 152370, 1, 0)) = 1\n" +
                "                               OR MAX(IF(TRIM(`openmrs`.`q`.`name`) = 'SPVC Follow up', 1, 0)) = 1\n" +
                "                              THEN COALESCE(\n" +
                "                                  MAX(IF(\n" +
                "                                      (v.concept_id IN (165205, 167255)\n" +
                "                                       AND v.value_coded IN (127910, 140790, 118686,\n" +
                "                                                             148143, 163065, 139108,\n" +
                "                                                             138447, 126582, 126302))\n" +
                "                                      OR (v.concept_id = 167255 AND v.value_coded = 152292),\n" +
                "                                      CASE v.value_coded\n" +
                "                                          WHEN 127910 THEN 'Rape'\n" +
                "                                          WHEN 140790 THEN 'Attempted Rape'\n" +
                "                                          WHEN 118686 THEN 'Defilement'\n" +
                "                                          WHEN 148143 THEN 'Attempted Defilement'\n" +
                "                                          WHEN 163065 THEN 'Indecent Act'\n" +
                "                                          WHEN 139108 THEN 'Sexual harassment'\n" +
                "                                          WHEN 138447 THEN 'Sodomy'\n" +
                "                                          WHEN 126582 THEN 'Sexual Assault'\n" +
                "                                          WHEN 152292 THEN 'Sexual Assault'\n" +
                "                                          WHEN 126302 THEN 'Intimate partner'\n" +
                "                                          ELSE NULL\n" +
                "                                      END,\n" +
                "                                      NULL\n" +
                "                                  )),\n" +
                "                                  'Sexual (type not specified)'\n" +
                "                              )\n" +
                "                          ELSE NULL\n" +
                "                      END AS `Sexual Violence Subtype`,\n" +
                "                        MAX(IF((`openmrs`.`v`.`concept_id` = 163488),\n" +
                "                                (CASE `openmrs`.`v`.`value_coded`\n" +
                "                                    WHEN 159372 THEN 'CCC'\n" +
                "                                    WHEN 160542 THEN 'OPD'\n" +
                "                                    WHEN 167050 THEN 'IPD'\n" +
                "                                    WHEN 1000479 THEN 'Community'\n" +
                "                                    WHEN 2002553 THEN 'Police'\n" +
                "                                    WHEN 2028434 THEN 'Children Office'\n" +
                "                                    WHEN 2028068 THEN 'School'\n" +
                "                                    WHEN 162690 THEN 'CHP'\n" +
                "                                    WHEN 160551 THEN 'Self Referral'\n" +
                "                                    WHEN 5622 THEN 'Other'\n" +
                "                                    ELSE ''\n" +
                "                                END),\n" +
                "                                NULL)) AS `Referred From`,\n" +
                "                        MAX(IF((`openmrs`.`v`.`concept_id` IN(163530,162165)),\n" +
                "                                TRIM(`openmrs`.`v`.`value_text`),\n" +
                "                                NULL)) AS `Other Referral Details`,\n" +
                "                        MAX(IF((`openmrs`.`v`.`concept_id` = 165229),\n" +
                "                                (CASE `openmrs`.`v`.`value_coded`\n" +
                "                                    WHEN 1067 THEN 'unknown'\n" +
                "                                    WHEN 166051 THEN 'known'\n" +
                "                                    ELSE ''\n" +
                "                                END),\n" +
                "                                NULL)) AS `Perpetrator Known Status`,\n" +
                "                        MAX(IF((`openmrs`.`v`.`concept_id` = 163541),\n" +
                "                                (CASE `openmrs`.`v`.`value_coded`\n" +
                "                                    WHEN 163562 THEN '18-24 yrs'\n" +
                "                                    WHEN 163332 THEN '25-34 yrs'\n" +
                "                                    WHEN 1734 THEN '35-49 yrs'\n" +
                "                                    WHEN 163328 THEN '50 yrs and above'\n" +
                "                                    WHEN 163563 THEN 'Less than 18 yrs'\n" +
                "                                    ELSE ''\n" +
                "                                END),\n" +
                "                                NULL)) AS `Perpetrator Age Group`,\n" +
                "                        MAX(IF((`openmrs`.`v`.`concept_id` = 1533),\n" +
                "                                (CASE `openmrs`.`v`.`value_coded`\n" +
                "                                    WHEN 1535 THEN 'Female'\n" +
                "                                    WHEN 1534 THEN 'Male'\n" +
                "                                    ELSE ''\n" +
                "                                END),\n" +
                "                                NULL)) AS `Perpetrator Sex`,\n" +
                "                        /***services offered for PVC cases****/\n" +
                "                      MAX(IF((`openmrs`.`v`.`concept_id` = 31),\n" +
                "                                (CASE `openmrs`.`v`.`value_coded`\n" +
                "                                    WHEN 160570 THEN 'HIV Testing'\n" +
                "                                    ELSE ''\n" +
                "                                END),\n" +
                "                                NULL)) AS `Physical/Emotional - HIV Testing Offered`,\n" +
                "                        MAX(IF((`openmrs`.`v`.`concept_id` = 31),\n" +
                "                                (CASE `openmrs`.`v`.`value_coded`\n" +
                "                                    WHEN 118668 THEN 'Pyschosocial'\n" +
                "                                    ELSE ''\n" +
                "                                END),\n" +
                "                                NULL)) AS `Physical/Emotional - Psychosocial Support Offered`,\n" +
                "                        MAX(IF((`openmrs`.`v`.`concept_id` = 31),\n" +
                "                                (CASE `openmrs`.`v`.`value_coded`\n" +
                "                                    WHEN 166536 THEN 'referral for Legai Aid'\n" +
                "                                    ELSE ''\n" +
                "                                END),\n" +
                "                                NULL)) AS `Physical/Emotional - Legal Aid Referral Offered`,\n" +
                "                        /***services offered for sPVC cases****/\n" +
                "                                MAX(IF((`openmrs`.`v`.`concept_id` = 165093),\n" +
                "                                (CASE `openmrs`.`v`.`value_coded`\n" +
                "                                    WHEN 160570 THEN 'Given ECP within 120 hrs'\n" +
                "                                    WHEN 118668 THEN 'STI Prophylaxis'\n" +
                "                                    WHEN 166536 THEN 'Given PEP within 72 hrs'\n" +
                "                                    WHEN 161194 THEN 'HTS'\n" +
                "                                    WHEN 2025973 THEN 'Hepatisis B'\n" +
                "                                    WHEN 159708 THEN 'HPV'\n" +
                "                                    WHEN 5488 THEN 'Adherence Counselling'\n" +
                "                                    WHEN 5490 THEN 'Psychosocial Support'\n" +
                "                                    WHEN 160551 THEN 'P3'\n" +
                "                                    WHEN 123154 THEN 'Referral'\n" +
                "                                    ELSE ''\n" +
                "                                END),\n" +
                "                                NULL)) AS `Sexual Violence - Services Offered`,\n" +
                "                        MAX(IF((`openmrs`.`v`.`concept_id` = 166586),\n" +
                "                                (CASE `openmrs`.`v`.`value_coded`\n" +
                "                                    WHEN 1065 THEN 'Yes'\n" +
                "                                    WHEN 1066 THEN 'No'\n" +
                "                                    ELSE ''\n" +
                "                                END),\n" +
                "                                NULL)) AS `Eligible for HIV Testing`,\n" +
                "                        MAX(IF((`openmrs`.`v`.`concept_id` = 159427),\n" +
                "                                (CASE `openmrs`.`v`.`value_coded`\n" +
                "                                   WHEN 1118 THEN 'Not done'\n" +
                "                                    WHEN 703 THEN 'Positive'\n" +
                "                                    WHEN 664 THEN 'Negative'\n" +
                "                                    WHEN 1065 THEN 'Known Positive'\n" +
                "                                    ELSE ''\n" +
                "                                END),\n" +
                "                                NULL)) AS `HIV Test Results`,\n" +
                "                        MAX(IF((`openmrs`.`v`.`concept_id` = 164845),\n" +
                "                                (CASE `openmrs`.`v`.`value_coded`\n" +
                "                                    WHEN 1065 THEN 'Yes'\n" +
                "                                    WHEN 1066 THEN 'No'\n" +
                "                                    ELSE ''\n" +
                "                                END),\n" +
                "                                NULL)) AS `Eligible for PEP`,\n" +
                "                        MAX(IF((`openmrs`.`v`.`concept_id` =  165060),\n" +
                "                                (CASE `openmrs`.`v`.`value_coded`\n" +
                "                                    WHEN 1065 THEN 'Yes'\n" +
                "                                    WHEN 1066 THEN 'No'\n" +
                "                                    ELSE ''\n" +
                "                                END),\n" +
                "                                NULL)) AS `Initiated on PEP`,\n" +
                "                        MAX(IF((`openmrs`.`v`.`concept_id` = 165249),\n" +
                "                                (CASE `openmrs`.`v`.`value_coded`\n" +
                "                                    WHEN 1065 THEN 'Yes'\n" +
                "                                    WHEN 1066 THEN 'No'\n" +
                "                                    ELSE ''\n" +
                "                                END),\n" +
                "                                NULL)) AS `Eligible for STI Services`,\n" +
                "                        MAX(IF((`openmrs`.`v`.`concept_id` =  165200),\n" +
                "                                (CASE `openmrs`.`v`.`value_coded`\n" +
                "                                    WHEN 1065 THEN 'Yes'\n" +
                "                                    WHEN 1066 THEN 'No'\n" +
                "                                    ELSE ''\n" +
                "                                END),\n" +
                "                                NULL)) AS `Initiated on STI Services`,\n" +
                "                        MAX(IF((`openmrs`.`v`.`concept_id` =  1670),\n" +
                "                                (CASE `openmrs`.`v`.`value_coded`\n" +
                "                                    WHEN 1065 THEN 'Yes'\n" +
                "                                    WHEN 1066 THEN 'No'\n" +
                "                                    ELSE ''\n" +
                "                                END),\n" +
                "                                NULL)) AS `Eligible for ECP`,\n" +
                "                        MAX(IF((`openmrs`.`v`.`concept_id` =  1669),\n" +
                "                                (CASE `openmrs`.`v`.`value_coded`\n" +
                "                                    WHEN 1065 THEN 'Yes'\n" +
                "                                    WHEN 1066 THEN 'No'\n" +
                "                                    ELSE ''\n" +
                "                                END),\n" +
                "                                NULL)) AS `Initiated on ECP`,\n" +
                "                        MAX(IF((`openmrs`.`v`.`concept_id` =  2032006),\n" +
                "                                (CASE `openmrs`.`v`.`value_coded`\n" +
                "                                    WHEN 1065 THEN 'Yes'\n" +
                "                                    WHEN 1066 THEN 'No'\n" +
                "                                    ELSE ''\n" +
                "                                END),\n" +
                "                                NULL)) AS `Eligible for Hepatitis B Services`,\n" +
                "                        MAX(IF((`openmrs`.`v`.`concept_id` =  2031734),\n" +
                "                                (CASE `openmrs`.`v`.`value_coded`\n" +
                "                                    WHEN 1065 THEN 'Yes'\n" +
                "                                    WHEN 1066 THEN 'No'\n" +
                "                                    ELSE ''\n" +
                "                                END),\n" +
                "                                NULL)) AS `Initiated on Hepatitis B Services`,\n" +
                "                        MAX(IF((`openmrs`.`v`.`concept_id` =  124957),\n" +
                "                                (CASE `openmrs`.`v`.`value_coded`\n" +
                "                                    WHEN 1065 THEN 'Yes'\n" +
                "                                    WHEN 1066 THEN 'No'\n" +
                "                                    ELSE ''\n" +
                "                                END),\n" +
                "                                NULL)) AS `Eligible for Tetanus Services`,\n" +
                "                        MAX(IF((`openmrs`.`v`.`concept_id` =  54),\n" +
                "                                (CASE `openmrs`.`v`.`value_coded`\n" +
                "                                    WHEN 1065 THEN 'Yes'\n" +
                "                                    WHEN 1066 THEN 'No'\n" +
                "                                    ELSE ''\n" +
                "                                END),\n" +
                "                                NULL)) AS `Tetanus Given`,\n" +
                "                        MAX(IF((`openmrs`.`v`.`concept_id` =  1000079),\n" +
                "                                (CASE `openmrs`.`v`.`value_coded`\n" +
                "                                    WHEN 1065 THEN 'Yes'\n" +
                "                                    WHEN 1066 THEN 'No'\n" +
                "                                    ELSE ''\n" +
                "                                END),\n" +
                "                                NULL)) AS `Eligible for HPV Services`,\n" +
                "                        MAX(IF((`openmrs`.`v`.`concept_id` =  167843),\n" +
                "                                (CASE `openmrs`.`v`.`value_coded`\n" +
                "                                    WHEN 1065 THEN 'Yes'\n" +
                "                                    WHEN 1066 THEN 'No'\n" +
                "                                    ELSE ''\n" +
                "                                END),\n" +
                "                                NULL)) AS `HPV Given`,\n" +
                "                        MAX(IF((`openmrs`.`v`.`concept_id` =  168823),\n" +
                "                                (CASE `openmrs`.`v`.`value_coded`\n" +
                "                                    WHEN 1065 THEN 'Yes'\n" +
                "                                    WHEN 1066 THEN 'No'\n" +
                "                                    ELSE ''\n" +
                "                                END),\n" +
                "                                NULL)) AS `Eligible for Psychosocial Support`,\n" +
                "                        MAX(IF((`openmrs`.`v`.`concept_id` =  167843),\n" +
                "                                (CASE `openmrs`.`v`.`value_coded`\n" +
                "                                    WHEN 1065 THEN 'Yes'\n" +
                "                                    WHEN 1066 THEN 'No'\n" +
                "                                    ELSE ''\n" +
                "                                END),\n" +
                "                                NULL)) AS `Psychosocial Support Provided`,\n" +
                "                        MAX(IF((`openmrs`.`v`.`concept_id` =  165104),\n" +
                "                                (CASE `openmrs`.`v`.`value_coded`\n" +
                "                                    WHEN 1065 THEN 'Yes'\n" +
                "                                    WHEN 1066 THEN 'No'\n" +
                "                                    ELSE ''\n" +
                "                                END),\n" +
                "                                NULL)) AS `Eligible for Adherence Counselling`,\n" +
                "                        MAX(IF((`openmrs`.`v`.`concept_id` =  166487),\n" +
                "                                (CASE `openmrs`.`v`.`value_coded`\n" +
                "                                    WHEN 1065 THEN 'Yes'\n" +
                "                                    WHEN 1066 THEN 'No'\n" +
                "                                    ELSE ''\n" +
                "                                END),\n" +
                "                                NULL)) AS `Adherence Counselling Offered`,\n" +
                "                        MAX(IF((`openmrs`.`v`.`concept_id` =  169279),\n" +
                "                                (CASE `openmrs`.`v`.`value_coded`\n" +
                "                                    WHEN 1065 THEN 'Yes'\n" +
                "                                    WHEN 1066 THEN 'No'\n" +
                "                                    ELSE ''\n" +
                "                                END),\n" +
                "                                NULL)) AS `Eligible for P3`,\n" +
                "                        MAX(IF((`openmrs`.`v`.`concept_id` =  165793),\n" +
                "                                (CASE `openmrs`.`v`.`value_coded`\n" +
                "                                    WHEN 1065 THEN 'Yes'\n" +
                "                                    WHEN 1066 THEN 'No'\n" +
                "                                    ELSE ''\n" +
                "                                END),\n" +
                "                                NULL)) AS `P3 Form Completed`,\n" +
                "                        MAX(IF((`openmrs`.`v`.`concept_id` = 1000031 ),\n" +
                "                                (CASE `openmrs`.`v`.`value_coded`\n" +
                "                                    WHEN 1065 THEN 'Yes'\n" +
                "                                    WHEN 1066 THEN 'No'\n" +
                "                                    ELSE ''\n" +
                "                                END),\n" +
                "                                NULL)) AS `Eligible for Referral Services`,\n" +
                "                        MAX(IF((`openmrs`.`v`.`concept_id` = 1000031 ),\n" +
                "                                (CASE `openmrs`.`v`.`value_coded`\n" +
                "                                    WHEN 664 THEN 'Legal AID'\n" +
                "                                    ELSE ''\n" +
                "                                END),\n" +
                "                                NULL)) AS `Referral - Legal Aid`,\n" +
                "                        MAX(IF((`openmrs`.`v`.`concept_id` = 1000031 ),\n" +
                "                                (CASE `openmrs`.`v`.`value_coded`\n" +
                "                                    WHEN 703 THEN 'shelter'\n" +
                "                                    ELSE ''\n" +
                "                                END),\n" +
                "                                NULL)) AS `Referral - Shelter`,\n" +
                "                        MAX(IF((`openmrs`.`v`.`concept_id` = 1000031 ),\n" +
                "                                (CASE `openmrs`.`v`.`value_coded`\n" +
                "                                    WHEN 1065 THEN 'Economic Empowerment'\n" +
                "                                    ELSE ''\n" +
                "                                END),\n" +
                "                                NULL)) AS `Referral - Economic Empowerment`,\n" +
                "                        MAX(IF((`openmrs`.`v`.`concept_id` = 1000031 ),\n" +
                "                                (CASE `openmrs`.`v`.`value_coded`\n" +
                "                                    WHEN 1528 THEN 'Child Protection'\n" +
                "                                    ELSE ''\n" +
                "                                END),\n" +
                "                                NULL)) AS `Referral - Child Protection`,\n" +
                "                        MAX(IF((`openmrs`.`v`.`concept_id` = 1000031 ),\n" +
                "                                (CASE `openmrs`.`v`.`value_coded`\n" +
                "                                    WHEN 1118 THEN 'others'\n" +
                "                                    ELSE ''\n" +
                "                                END),\n" +
                "                                NULL)) AS `Referral - Other`,\n" +
                "                        MAX(IF((`openmrs`.`v`.`concept_id` = 162166),\n" +
                "                                TRIM(`openmrs`.`v`.`value_text`),\n" +
                "                                NULL)) AS `Case Notes`,\n" +
                "                        /***Followup visits***/\n" +
                "                                      MAX(IF((`openmrs`.`v`.`concept_id` = 164181),\n" +
                "                                (CASE `openmrs`.`v`.`value_coded`\n" +
                "                                    WHEN 163332 THEN '2nd visit'\n" +
                "                                    WHEN 1734 THEN '3rd Visit'\n" +
                "                                    WHEN 163328 THEN '4th Visit'\n" +
                "                                    WHEN 163563 THEN '5th Visit'\n" +
                "                                    ELSE ''\n" +
                "                                END),\n" +
                "                                NULL)) AS `Follow-up Visit Number`,\n" +
                "                        /***2nd Visit**/\n" +
                "                            MAX(IF((`openmrs`.`v`.`concept_id` = 166848),\n" +
                "                                TRIM(`openmrs`.`v`.`value_datetime`),\n" +
                "                                NULL)) AS `Visit 2 - Actual Return Date`,\n" +
                "                        MAX(IF((`openmrs`.`v`.`concept_id` = 166665 ),\n" +
                "                                (CASE `openmrs`.`v`.`value_coded`\n" +
                "                                    WHEN 1065 THEN 'Given'\n" +
                "                                    WHEN 1066 THEN 'Not Given'\n" +
                "                                    ELSE ''\n" +
                "                                END),\n" +
                "                                NULL)) AS `Visit 2 - PEP Adherence`,\n" +
                "                        MAX(IF((`openmrs`.`v`.`concept_id` = 165353),\n" +
                "                                (CASE `openmrs`.`v`.`value_coded`\n" +
                "                                    WHEN 1065 THEN 'Given'\n" +
                "                                    WHEN 1066 THEN 'Not Given'\n" +
                "                                    ELSE ''\n" +
                "                                END),\n" +
                "                                NULL)) AS `Visit 2 - Psychosocial Support`,\n" +
                "                        MAX(IF((`openmrs`.`v`.`concept_id` = 2031563),\n" +
                "                                (CASE `openmrs`.`v`.`value_coded`\n" +
                "                                    WHEN 1065 THEN 'Given'\n" +
                "                                    WHEN 1066 THEN 'Not Given'\n" +
                "                                    ELSE ''\n" +
                "                                END),\n" +
                "                                NULL)) AS `Visit 2 - Referral Uptake`,\n" +
                "                        MAX(IF((`openmrs`.`v`.`concept_id` = 160753),\n" +
                "                                TRIM(`openmrs`.`v`.`value_datetime`),\n" +
                "                                NULL)) AS `Visit 2 - Next Appointment`,\n" +
                "                        /***3rd Visit**/\n" +
                "                                        MAX(IF((`openmrs`.`v`.`concept_id` = 163260),\n" +
                "                                TRIM(`openmrs`.`v`.`value_datetime`),\n" +
                "                                NULL)) AS `Visit 3 - Actual Return Date`,\n" +
                "                        MAX(IF((`openmrs`.`v`.`concept_id` = 165233),\n" +
                "                                (CASE `openmrs`.`v`.`value_coded`\n" +
                "                                    WHEN 138571 THEN 'Positive'\n" +
                "                                    WHEN 664 THEN 'Negative'\n" +
                "                                    WHEN 1118 THEN 'Not Done'\n" +
                "                                    ELSE ''\n" +
                "                                END),\n" +
                "                                NULL)) AS `Visit 3 - HIV Test Result`,\n" +
                "                        MAX(IF((`openmrs`.`v`.`concept_id` = 1945),\n" +
                "                                (CASE `openmrs`.`v`.`value_coded`\n" +
                "                                    WHEN 1065 THEN 'Yes'\n" +
                "                                    WHEN 1066 THEN 'No'\n" +
                "                                    ELSE ''\n" +
                "                                END),\n" +
                "                                NULL)) AS `Visit 3 - Pregnancy Test Done`,\n" +
                "                        MAX(IF((`openmrs`.`v`.`concept_id` = 165128),\n" +
                "                                (CASE `openmrs`.`v`.`value_coded`\n" +
                "                                    WHEN 1065 THEN 'Yes'\n" +
                "                                    WHEN 1066 THEN 'No'\n" +
                "                                    ELSE ''\n" +
                "                                END),\n" +
                "                                NULL)) AS `Visit 3 - Referral Uptake`,\n" +
                "                        MAX(IF((`openmrs`.`v`.`concept_id` = 2032241),\n" +
                "                                (CASE `openmrs`.`v`.`value_coded`\n" +
                "                                    WHEN 1065 THEN 'Yes'\n" +
                "                                    WHEN 1066 THEN 'No'\n" +
                "                                    ELSE ''\n" +
                "                                END),\n" +
                "                                NULL)) AS `Visit 3 - PEP Completion`,\n" +
                "                        MAX(IF((`openmrs`.`v`.`concept_id` = 2032975),\n" +
                "                                TRIM(`openmrs`.`v`.`value_datetime`),\n" +
                "                                NULL)) AS `Visit 3 - Next Appointment`,\n" +
                "                        /**4th Visit**/\n" +
                "                                        MAX(IF((`openmrs`.`v`.`concept_id` = 164891),\n" +
                "                                TRIM(`openmrs`.`v`.`value_datetime`),\n" +
                "                                NULL)) AS `Visit 4 - Actual Return Date`,\n" +
                "                        MAX(IF((`openmrs`.`v`.`concept_id` = 2031577),\n" +
                "                                (CASE `openmrs`.`v`.`value_coded`\n" +
                "                                    WHEN 1065 THEN 'Given'\n" +
                "                                    WHEN 1066 THEN 'Not Given'\n" +
                "                                    ELSE ''\n" +
                "                                END),\n" +
                "                                NULL)) AS `Visit 4 - Hepatitis B Vaccine`,\n" +
                "                        MAX(IF((`openmrs`.`v`.`concept_id` = 167093),\n" +
                "                                (CASE `openmrs`.`v`.`value_coded`\n" +
                "                                    WHEN 1065 THEN 'Yes'\n" +
                "                                    WHEN 1066 THEN 'No'\n" +
                "                                    ELSE ''\n" +
                "                                END),\n" +
                "                                NULL)) AS `Visit 4 - Psychosocial Support`,\n" +
                "                        MAX(IF((`openmrs`.`v`.`concept_id` = 159777),\n" +
                "                                (CASE `openmrs`.`v`.`value_coded`\n" +
                "                                    WHEN 1065 THEN 'Yes'\n" +
                "                                    WHEN 1066 THEN 'No'\n" +
                "                                    ELSE ''\n" +
                "                                END),\n" +
                "                                NULL)) AS `Visit 4 - Referral Uptake`,\n" +
                "                        MAX(IF((`openmrs`.`v`.`concept_id` = 1410),\n" +
                "                                TRIM(`openmrs`.`v`.`value_datetime`),\n" +
                "                                NULL)) AS `Visit 4 - Next Appointment`,\n" +
                "                        /***5th Visit**/\n" +
                "                                  MAX(IF((`openmrs`.`v`.`concept_id` = 162572),\n" +
                "                                TRIM(`openmrs`.`v`.`value_datetime`),\n" +
                "                                NULL)) AS `Visit 5 - Actual Return Date`,\n" +
                "                        MAX(IF((`openmrs`.`v`.`concept_id` = 1356),\n" +
                "                                (CASE `openmrs`.`v`.`value_coded`\n" +
                "                                    WHEN 703 THEN 'Positive'\n" +
                "                                    WHEN 664 THEN 'Negative'\n" +
                "                                    WHEN 1118 THEN 'Not Done'\n" +
                "                                    ELSE ''\n" +
                "                                END),\n" +
                "                                NULL)) AS `Visit 5 - HIV Test Result`,\n" +
                "                        MAX(IF((`openmrs`.`v`.`concept_id` = 165127),\n" +
                "                                (CASE `openmrs`.`v`.`value_coded`\n" +
                "                                    WHEN 1065 THEN 'Yes'\n" +
                "                                    WHEN 1066 THEN 'No'\n" +
                "                                    ELSE ''\n" +
                "                                END),\n" +
                "                                NULL)) AS `Visit 5 - Psychosocial Support`,\n" +
                "                        MAX(IF((`openmrs`.`v`.`concept_id` = 165221),\n" +
                "                                (CASE `openmrs`.`v`.`value_coded`\n" +
                "                                    WHEN 1065 THEN 'Yes'\n" +
                "                                    WHEN 1066 THEN 'No'\n" +
                "                                    ELSE ''\n" +
                "                                END),\n" +
                "                                NULL)) AS `Visit 5 - Referral Uptake`,\n" +
                "                        MAX(IF((`openmrs`.`v`.`concept_id` = 1000172),\n" +
                "                                TRIM(`openmrs`.`v`.`value_datetime`),\n" +
                "                                NULL)) AS `Visit 5 - Next Appointment`,\n" +
                "                        /***Legal AID Section***/\n" +
                "                    MAX(IF((`openmrs`.`v`.`concept_id` = 2002338),\n" +
                "                                TRIM(`openmrs`.`v`.`value_text`),\n" +
                "                                NULL)) AS `Court File Number`,\n" +
                "                        MAX(IF((`v`.`concept_id` =  165144),\n" +
                "                                TRIM(`v`.`value_datetime`),\n" +
                "                                NULL)) AS `Report Date`,\n" +
                "                        MAX(IF((`v`.`concept_id` =  160753),\n" +
                "                                TRIM(`v`.`value_datetime`),\n" +
                "                                NULL)) AS `Date Bond Received`,\n" +
                "                        MAX(IF((`v`.`concept_id` = 168333),\n" +
                "                                (CASE `v`.`value_coded`\n" +
                "                                    WHEN 1065 THEN 'Yes'\n" +
                "                                    WHEN 1066 THEN 'No'\n" +
                "                                    WHEN 1067 THEN 'Unknown'\n" +
                "                                    ELSE ''\n" +
                "                                END),\n" +
                "                                NULL)) AS `Services Offered to Perpetrator`,\n" +
                "                        MAX(IF((`v`.`concept_id` = 167823),\n" +
                "                                  (CASE `v`.`value_coded`\n" +
                "                                    WHEN 165179 THEN 'Police Investigation'\n" +
                "                                    WHEN 167667 THEN 'Court Hearing'\n" +
                "                                    WHEN 418 THEN 'Ruling'\n" +
                "                                    WHEN 2014522 THEN 'Judgement'\n" +
                "                                    WHEN 2022534 THEN 'Acquittal'\n" +
                "                                    WHEN 163495 THEN 'Withdrawal'\n" +
                "                                    WHEN 166536 then 'Sentencing'\n" +
                "                                    WHEN 5490 then 'Case pending'\n" +
                "                    WHEN 123154 then 'Adjournment'\n" +
                "                                    ELSE ''\n" +
                "                                END),\n" +
                "                                NULL)) AS `Legal Case Status`,\n" +
                "                        MAX(IF((`v`.`concept_id` = 167116),\n" +
                "                                (CASE `v`.`value_coded`\n" +
                "                                    WHEN 162725 THEN 'Acquittal'\n" +
                "                                    WHEN 2014000 THEN 'Conviction'\n" +
                "                                    ELSE ''\n" +
                "                                END),\n" +
                "                                NULL)) AS `Final Judgement`,\n" +
                "                        MAX(IF((`openmrs`.`v`.`concept_id` = 159791),\n" +
                "                                TRIM(`openmrs`.`v`.`value_text`),\n" +
                "                                NULL)) AS `Duration of Conviction`,\n" +
                "                        MAX(IF((`openmrs`.`v`.`concept_id` =2002154),\n" +
                "                                TRIM(`openmrs`.`v`.`value_text`),\n" +
                "                                NULL)) AS `Final Case Outcome`\n" +
                "                    FROM\n" +
                "                            ((((((`openmrs`.`obs` `v`\n" +
                "                            JOIN `openmrs`.`person` `n` ON (((`openmrs`.`v`.`person_id` = `openmrs`.`n`.`person_id`)\n" +
                "                                AND (`openmrs`.`v`.`voided` = 0))))\n" +
                "                            JOIN `openmrs`.`patient` `b` ON ((`openmrs`.`b`.`patient_id` = `openmrs`.`n`.`person_id`)))\n" +
                "                            JOIN `openmrs`.`encounter` `r` ON ((`openmrs`.`r`.`encounter_id` = `openmrs`.`v`.`encounter_id`)))\n" +
                "                            JOIN `openmrs`.`form` `q` ON ((`openmrs`.`q`.`encounter_type` = `openmrs`.`r`.`encounter_type`)))\n" +
                "                            JOIN `openmrs`.`patient_identifier` `c` ON ((`openmrs`.`b`.`patient_id` = `openmrs`.`c`.`patient_id`))))\n" +
                "                        WHERE\n" +
                "                            ((`openmrs`.`r`.`encounter_datetime` BETWEEN  :startDate AND :endDate)\n" +
                "                                AND (`openmrs`.`q`.`uuid` in ( '8d056a0b-9f8e-4a47-84c0-47bcd5f34534', '5f48c23d-a859-4446-86c7-4282b0516559')))\n" +
                "                        GROUP BY `openmrs`.`r`.`encounter_id`;");

        return dsd;

    }

}