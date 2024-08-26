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
@Builds({ "kenyaemr.coptic.module.report.gbv" })

public class GBVReportBuilder extends  AbstractReportBuilder {

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
        dsd.setName("gbv");
        dsd.setDescription("GBV Line list");
        dsd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        dsd.addParameter(new Parameter("endDate", "End Date", Date.class));

        dsd.setSqlQuery("select\n" +
                "                concat_ws(\"   \", pn.given_name, pn.family_name) as Patient_name,\n" +
                "                e.patient_id as PID,\n" +
                "                e.location_id as LID,\n" +
                "                date(e.encounter_datetime) as \"Visit Date\",\n" +
                "                max(if(o.concept_id=5096,o.value_datetime,null)) as \"Incident Date\",\n" +
                "                max(if(o.concept_id=165205,(case o.value_coded when 118668 then \"Emotional\" when 152292 then \"Physical\" else \"\" end),null)) as \"Type of Violence\" ,\n" +
                "                max(if(o.concept_id=163488,(case o.value_coded when 159372 then \"CC\" else \"\" end),null)) as \"Referred from\",\n" +
                "                max(if(o.concept_id=163488,(case o.value_coded when 160542 then \"OPD\" else \"\" end),null)) as \"CCC Number\",\n" +
                "                max(if(o.concept_id=163530,(case o.value_coded when 67050 then \"IPD\" else \"\" end),null)) as \"Medical Records\",\n" +
                "                max(if(o.concept_id=163488,(case o.value_coded when 5622 then \"Other\" else \"\" end),null)) as \"Other\",\n" +
                "                max(if(o.concept_id=163530,o.value_text,null)) as \"Specify\",\n" +
                "                max(if(o.concept_id=159427,(case o.value_coded when 664 then \"Negative\" when 703 then \"Positive\" when 1065 then \"Known Positive\" when 1118 then \"Not done\" else \"\" end),null)) as \"HIV Test Results\",\n" +
                "                max(if(o.concept_id=165229,(case o.value_coded when 166051 then \"Known\" when 1067 then \"Unknown\" else \"\" end),null)) as \"Perpetrator\",\n" +
                "                max(if(o.concept_id=163541,(case o.value_coded when 163563 then \"Less than 18 yrs\" when 163562 then \"18-24 yrs\" when 163332 then \"25-34\" when 1734 then \"35-49 yrs\" when 163328 then \"50 yrs and above\" else \"\" end),null)) as \"Perpetrator Age\",\n" +
                "                max(if(o.concept_id=1533,(case o.value_coded when 1534 then \"Male\" when 1535 then \"Female\" else \"\" end),null)) as \"Perpetrator Gender\",\n" +
                "                max(if(o.concept_id=162166,o.value_text,null)) as Notes,\n" +
                "\t\t\t\tconcat_ws(\" \",pnn.given_name, pnn.family_name) as \"Provider Name\"\n" +
                "\n" +
                "from openmrs.encounter e\n" +
                "                inner join openmrs.person_name pn on pn.person_id=e.patient_id and pn.voided=0\n" +
                "                inner join openmrs.patient_identifier pi  on pi.patient_id=e.patient_id and pn.voided=0 \n" +
                "                inner join openmrs.person p on p.person_id=e.patient_id and p.voided=0\n" +
                "                inner join openmrs.encounter_provider ep on e.encounter_id = ep.encounter_id\n" +
                "                inner join openmrs.provider pv on pv.provider_id = ep.provider_id\n" +
                "                inner join openmrs.person_name pnn on pnn.person_id = pv.person_id\n" +
                "                inner join\n" +
                "                (\n" +
                "                select form_id from openmrs.form where\n" +
                "                uuid in('8d056a0b-9f8e-4a47-84c0-47bcd5f34534  ')\n" +
                "                ) f on f.form_id=e.form_id\n" +
                "                left outer join openmrs.obs o on o.encounter_id=e.encounter_id and o.voided=0\n" +
                "                 and o.concept_id in (5096,165205,163488,163488,163530,163488,163530,159427,165229,163541,1533,162166)\n" +
                "where e.encounter_datetime between date(:startDate) and date(:endDate) and e.voided=0\n" +
                "group by e.patient_id, e.encounter_id;");

        return dsd;

    }

}