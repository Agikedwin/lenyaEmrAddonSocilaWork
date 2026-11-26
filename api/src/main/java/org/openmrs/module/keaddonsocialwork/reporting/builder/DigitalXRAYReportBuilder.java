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
@Builds({ "kenyaemr.coptic.module.report.digitalXRay"})
public class DigitalXRAYReportBuilder extends AbstractReportBuilder {

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
        dsd.setName("digitalXRay");
        dsd.setDescription("Digital X-RAY Report");
        //dsd.addSortCriteria("Enrollment Date", SortCriteria.SortDirection.ASC);
        dsd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        dsd.addParameter(new Parameter("endDate", "End Date", Date.class));
        String paramMapping = "startDate=${startDate},endDate=${endDate}";
        dsd.setSqlQuery("SELECT\n" +
                "                    \n" +
                "                    CONCAT_WS(\" \", pn.given_name, pn.family_name) AS \"Client Name\",\n" +
                "                    MAX(IF(pit.uuid='dfacd928-0370-4315-99d7-6ec1c9f7ae76', pi.identifier, NULL)) AS \"Openmrs Id\",\n" +
                "                    MAX(IF(o.concept_id=164932, CASE o.value_coded\n" +
                "                        WHEN 160015 THEN \"Newly diagnosed\"\n" +
                "                        WHEN 164144 THEN \"CD4 &lt; 200 cells/mm³\"\n" +
                "                        WHEN 159833 THEN \"Returning to care\"\n" +
                "                        WHEN 159961 THEN \"With TB symptoms\"\n" +
                "                        WHEN 160542 THEN \"TB presumed OPT\"\n" +
                "                        WHEN 160536 THEN \"TB Presumed IPD\"\n" +
                "                        WHEN 160563 THEN \"Transfer-In on TB treatment\"\n" +
                "                        WHEN 165649 THEN \"Symptomatic TB contacts\"\n" +
                "                        WHEN 164931 THEN \"Referral from other facility\" ELSE \"\" END, NULL)) AS \"Client Type\",\n" +
                "                    MAX(IF(o.concept_id=165142, o.value_text, NULL)) AS \"Other Client Type\",\n" +
                "                    MAX(IF(o.concept_id=165416, o.value_numeric, NULL)) AS \"CAD Score\",\n" +
                "                    MAX(IF(o.concept_id=160108, CASE o.value_coded\n" +
                "                        WHEN 161297 THEN \"Normal CXR\"\n" +
                "                        WHEN 114100 THEN \"Normal CXR but client symptomatic for TB\"\n" +
                "                        WHEN 165649 THEN \"Abnormal CXR\" ELSE \"\" END, NULL)) AS \"CXR Results\",\n" +
                "                    MAX(IF(o.concept_id=5219, CASE o.value_coded\n" +
                "                        WHEN 151 THEN \"suggestive of TB\"\n" +
                "                        WHEN 114100 THEN \"Pneumonia\"\n" +
                "                        WHEN 120749 THEN \"Asthma\"\n" +
                "                        WHEN 871 THEN \"Pneumothorax\"\n" +
                "                        WHEN 120345 THEN \"Pulmonary Fibrosis\"\n" +
                "                        WHEN 161297 THEN \"Cardiomegaly\"\n" +
                "                        WHEN 113054 THEN \"Heart Failure\"\n" +
                "                        WHEN 144576 THEN \"Silicosis\"\n" +
                "                        WHEN 106 THEN \"Rib fracture\"\n" +
                "                        WHEN 160737 THEN \"Pleural Effusion\"\n" +
                "                        WHEN 143129 THEN \"Lung nodule\"\n" +
                "                        WHEN 119537 THEN \"Other(specify)\" ELSE \"\" END, NULL)) AS \"Abnormal Cxr Options\",\n" +
                "                    MAX(IF(o.concept_id=160632, o.value_text, NULL)) AS \"Abnormal Cxr Options Other\",\n" +
                "                    MAX(IF(o.concept_id=5219, CASE o.value_coded\n" +
                "                        WHEN 105281 THEN \"GeneXpert testing\"\n" +
                "                        WHEN 74250 THEN \"Sputum for AAFBs\"\n" +
                "                        WHEN 1107 THEN \"Biopsy\"\n" +
                "                        WHEN 120345 THEN \"Culture and sensitivity test\" ELSE \"\" END, NULL)) AS \"Cad Score Symptomatic\",\n" +
                "                    MAX(IF(o.concept_id=161652, CASE o.value_coded\n" +
                "                        WHEN 159405 THEN \"MTB Detected\"\n" +
                "                        WHEN 159407 THEN \"MTB not detected\"\n" +
                "                        WHEN 160737 THEN \"Not Done\" ELSE \"\" END, NULL)) AS \"GeneXpert Testing\",\n" +
                "                    MAX(IF(o.concept_id=162229, CASE o.value_coded\n" +
                "                        WHEN 703 THEN \"Positive\"\n" +
                "                        WHEN 664 THEN \"Negative\"\n" +
                "                        WHEN 160737 THEN \"Not Done\" ELSE \"\" END, NULL)) AS \"Sputum AAFBs\",\n" +
                "                    MAX(IF(o.concept_id=162737, CASE o.value_coded\n" +
                "                        WHEN 703 THEN \"Positive\"\n" +
                "                        WHEN 664 THEN \"Negative\"\n" +
                "                        WHEN 160737 THEN \"Not Done\" ELSE \"\" END, NULL)) AS \"Biopsy\",\n" +
                "                    MAX(IF(o.concept_id=162737, CASE o.value_coded\n" +
                "                        WHEN 703 THEN \"Growth\"\n" +
                "                        WHEN 664 THEN \"No growth\"\n" +
                "                        WHEN 160737 THEN \"Not Done\" ELSE \"\" END, NULL)) AS \"Culture Sensitivity\",\n" +
                "                    MAX(IF(o.concept_id=159394, CASE o.value_coded\n" +
                "                        WHEN 1065 THEN \"Yes\"\n" +
                "                        WHEN 1066 THEN \"No\" ELSE \"\" END, NULL)) AS \"Diagnosed with TB\",\n" +
                "                    MAX(IF(o.concept_id=163752, CASE o.value_coded\n" +
                "                        WHEN 1065 THEN \"Yes\"\n" +
                "                        WHEN 1066 THEN \"No\" ELSE \"\" END, NULL)) AS \"TB Treatment Started\",\n" +
                "                    MAX(IF(o.concept_id=163042, o.value_text, NULL)) AS Comment,\n" +
                "                    CONCAT_WS(\" \", pnn.given_name, pnn.family_name) AS Provider\n" +
                "                FROM encounter e\n" +
                "                INNER JOIN person p ON p.person_id = e.patient_id AND p.voided = 0\n" +
                "                INNER JOIN openmrs.person_name pn ON pn.person_id = e.patient_id AND p.voided = 0\n" +
                "                INNER JOIN openmrs.encounter_provider ep ON e.encounter_id = ep.encounter_id\n" +
                "                INNER JOIN openmrs.provider pv ON pv.provider_id = ep.provider_id\n" +
                "                INNER JOIN openmrs.person_name pnn ON pnn.person_id = pv.person_id\n" +
                "                INNER JOIN patient_identifier pi ON pi.patient_id = e.patient_id\n" +
                "                INNER JOIN patient_identifier_type pit ON pi.identifier_type = pit.patient_identifier_type_id\n" +
                "                INNER JOIN (\n" +
                "                    SELECT form_id\n" +
                "                    FROM form\n" +
                "                    WHERE uuid IN ('06d3291c-6bf4-4c1f-8429-5fca7ce004c8')\n" +
                "                ) f ON f.form_id = e.form_id\n" +
                "                LEFT OUTER JOIN obs o ON o.encounter_id = e.encounter_id\n" +
                "                    AND o.voided = 0\n" +
                "                    AND o.concept_id IN (\n" +
                "                        164932, 160108, 1000031, 161550, 165416, 168130, 5219, 160632,\n" +
                "                        1109, 161652, 162229, 162737, 159394, 163752, 163042\n" +
                "                    )\n" +
                "                GROUP BY e.patient_id, e.encounter_id;");

        return dsd;

    }

}