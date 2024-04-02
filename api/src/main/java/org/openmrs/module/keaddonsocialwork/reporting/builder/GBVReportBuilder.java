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
        dsd.setName("GBV Report");
        dsd.setDescription("GBV Line list");
        dsd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        dsd.addParameter(new Parameter("endDate", "End Date", Date.class));

        dsd.setSqlQuery("select\n" +
                "    concat_ws(\"   \", pn.given_name, pn.family_name) as Patient_name,\n" +
                "\te.patient_id,\n" +
                "\te.location_id,\n" +
                "\tdate(e.encounter_datetime) as visit_date,\n" +
                "\te.creator as encounter_provider,\n" +
                "    max(if(o.concept_id=5096,o.value_datetime,null)) as Incident_Date,\n" +
                "    max(if(o.concept_id=165205,(case o.value_coded when 118668 then \"Emotional\" when 152292 then \"Physical\" else \"\" end),null)) as Type_of_violence ,\n" +
                "    max(if(o.concept_id=163488,(case o.value_coded when 159372 then \"CC\" else \"\" end),null)) as Referred_from,\n" +
                "    max(if(o.concept_id=163488,(case o.value_coded when 160542 then \"OPD\" else \"\" end),null)) as Indicate_CCC_number,\n" +
                "    max(if(o.concept_id=163530,(case o.value_coded when 67050 then \"IPD\" else \"\" end),null)) as Indicate_Medical_Records_Number,\n" +
                "    max(if(o.concept_id=163488,(case o.value_coded when 5622 then \"Other\" else \"\" end),null)) as Indicate_Medical_Records_Number,\n" +
                "    max(if(o.concept_id=163530,o.value_text,null)) as Specify,\n" +
                "    max(if(o.concept_id=159427,(case o.value_coded when 664 then \"Negative\" when 703 then \"Positive\" when 1065 then \"Known Positive\" when 1118 then \"Not done\" else \"\" end),null)) as HIV_test_Results,\n" +
                "\tmax(if(o.concept_id=165229,(case o.value_coded when 166051 then \"Known\" when 1067 then \"Unknown\" else \"\" end),null)) as who_are_the_perpetrators,\n" +
                "\tmax(if(o.concept_id=163541,(case o.value_coded when 163563 then \"Less than 18 yrs\" when 163562 then \"18-24 yrs\" when 163332 then \"25-34\" when 1734 then \"35-49 yrs\" when 163328 then \"50 yrs and above\" else \"\" end),null)) as Age_of_perpetrator,\n" +
                "\tmax(if(o.concept_id=1533,(case o.value_coded when 1534 then \"Male\" when 1535 then \"Female\" else \"\" end),null)) as Gender_of_the_perpetrator,\n" +
                "\tmax(if(o.concept_id=162166,o.value_text,null)) as Notes_,\n" +
                "    \n" +
                "\t\n" +
                "\n" +
                "     \te.date_created as date_created,\n" +
                "\n" +
                "    e.voided as voided\n" +
                "from openmrs.encounter e\n" +
                "\tinner join openmrs.person p on p.person_id=e.patient_id and p.voided=0\n" +
                "    inner join openmrs.person_name pn on pn.person_id=e.patient_id and p.voided=0\n" +
                "\tinner join\n" +
                "\t(\n" +
                "\t\tselect form_id from openmrs.form where\n" +
                "\t\t\tuuid in('8d056a0b-9f8e-4a47-84c0-47bcd5f34534  ')\n" +
                "\t) f on f.form_id=e.form_id\n" +
                "\tleft outer join openmrs.obs o on o.encounter_id=e.encounter_id and o.voided=0\n" +
                "\t and o.concept_id in (5096,165205,163488,163488,163530,163488,163530,159427,165229,163541,1533,162166)\n" +
                "where e.voided=0 \n" +
                "group by e.patient_id, e.encounter_id;");

        return dsd;

    }

}