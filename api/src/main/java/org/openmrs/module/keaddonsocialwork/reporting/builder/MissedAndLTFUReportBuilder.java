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
@Builds({ "kenyaemr.coptic.module.report.missedAndLTFU"})
public class MissedAndLTFUReportBuilder extends AbstractReportBuilder {

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
        dsd.setName("missedAndLTFU");
        dsd.setDescription("Missed And LFTU Report");
        //dsd.addSortCriteria("Enrollment Date", SortCriteria.SortDirection.ASC);
        dsd.addParameter(new Parameter("startDate", "Start Date", Date.class));
        dsd.addParameter(new Parameter("endDate", "End Date", Date.class));
        String paramMapping = "startDate=${startDate},endDate=${endDate}";
        dsd.setSqlQuery("WITH\n" +
                "eligible AS (\n" +
                "    SELECT DISTINCT\n" +
                "        pa.patient_id\n" +
                "    FROM patient_appointment pa\n" +
                "    WHERE pa.appointment_service_id IN (1, 2)\n" +
                "      AND pa.start_date_time >= :startDate\n" +
                "      AND pa.start_date_time < DATE_ADD(:endDate, INTERVAL 1 DAY)\n" +
                "),\n" +
                "\n" +
                "app_ranked AS (\n" +
                "    SELECT\n" +
                "        pa.patient_id,\n" +
                "        pa.date_appointment_scheduled,\n" +
                "        pa.start_date_time,\n" +
                "        psa.value AS contact,\n" +
                "        ge.date_created AS green_card_date,\n" +
                "\n" +
                "        ROW_NUMBER() OVER (\n" +
                "            PARTITION BY pa.patient_id\n" +
                "            ORDER BY\n" +
                "                pa.start_date_time DESC,\n" +
                "                pa.patient_appointment_id DESC,\n" +
                "                ge.date_created DESC,\n" +
                "                ge.encounter_id DESC\n" +
                "        ) AS rn\n" +
                "\n" +
                "    FROM eligible el\n" +
                "\n" +
                "    JOIN patient_appointment pa\n" +
                "        ON pa.patient_id = el.patient_id\n" +
                "       AND pa.appointment_service_id = 1\n" +
                "       AND pa.start_date_time >= :startDate\n" +
                "       AND pa.start_date_time < DATE_ADD(:endDate, INTERVAL 1 DAY)\n" +
                "       AND pa.status <> 'Completed'\n" +
                "\n" +
                "    JOIN encounter ge\n" +
                "        ON ge.patient_id = pa.patient_id\n" +
                "       AND ge.encounter_type IN (8, 28)\n" +
                "       AND ge.voided = 0\n" +
                "       AND ge.date_created >= DATE(pa.date_appointment_scheduled)\n" +
                "       AND ge.date_created < DATE(pa.start_date_time)\n" +
                "\n" +
                "    JOIN person_attribute psa\n" +
                "        ON psa.person_id = pa.patient_id\n" +
                "       AND psa.person_attribute_type_id = 8\n" +
                "       AND psa.voided = 0\n" +
                "),\n" +
                "\n" +
                "app AS (\n" +
                "    SELECT\n" +
                "        patient_id,\n" +
                "        date_appointment_scheduled,\n" +
                "        start_date_time,\n" +
                "        contact,\n" +
                "        green_card_date\n" +
                "    FROM app_ranked\n" +
                "    WHERE rn = 1\n" +
                "),\n" +
                "\n" +
                "dispense_ranked AS (\n" +
                "    SELECT\n" +
                "        md.patient_id,\n" +
                "        md.quantity AS dispensed_quantity,\n" +
                "        md.date_created AS date_dispensed,\n" +
                "\n" +
                "        ROW_NUMBER() OVER (\n" +
                "            PARTITION BY md.patient_id\n" +
                "            ORDER BY md.date_created DESC\n" +
                "        ) AS rn\n" +
                "\n" +
                "    FROM app\n" +
                "\n" +
                "    JOIN medication_dispense md\n" +
                "        ON md.patient_id = app.patient_id\n" +
                "       AND md.status = 1267\n" +
                "),\n" +
                "\n" +
                "dp AS (\n" +
                "    SELECT\n" +
                "        patient_id,\n" +
                "        dispensed_quantity,\n" +
                "        date_dispensed\n" +
                "    FROM dispense_ranked\n" +
                "    WHERE rn = 1\n" +
                "),\n" +
                "\n" +
                "qualified AS (\n" +
                "    SELECT\n" +
                "        app.patient_id,\n" +
                "        app.date_appointment_scheduled,\n" +
                "        app.start_date_time,\n" +
                "        app.contact,\n" +
                "        app.green_card_date,\n" +
                "        dp.dispensed_quantity,\n" +
                "        dp.date_dispensed\n" +
                "\n" +
                "    FROM app\n" +
                "\n" +
                "    JOIN dp\n" +
                "        ON dp.patient_id = app.patient_id\n" +
                "\n" +
                "    WHERE (\n" +
                "        DATEDIFF(\n" +
                "            app.start_date_time,\n" +
                "            app.date_appointment_scheduled\n" +
                "        )\n" +
                "        -\n" +
                "        DATEDIFF(\n" +
                "            :endDate,\n" +
                "            dp.date_dispensed\n" +
                "        )\n" +
                "    ) < 0\n" +
                "\n" +
                "      AND dp.date_dispensed < DATE(app.start_date_time)\n" +
                "),\n" +
                "\n" +
                "dis_grouped AS (\n" +
                "    SELECT\n" +
                "        e.patient_id,\n" +
                "        e.encounter_id,\n" +
                "        e.encounter_datetime,\n" +
                "\n" +
                "        MAX(\n" +
                "            CASE\n" +
                "                WHEN o.concept_id = 161555\n" +
                "                THEN\n" +
                "                    CASE o.value_coded\n" +
                "\n" +
                "                        WHEN 5240\n" +
                "                            THEN 'Lost to Follow'\n" +
                "\n" +
                "                        WHEN '164349'\n" +
                "                            THEN 'Stopped Treatment'\n" +
                "\n" +
                "                        WHEN 159492\n" +
                "                            THEN 'Transferred Out'\n" +
                "\n" +
                "                        WHEN 167332\n" +
                "                            THEN 'Receiving ART in multiple sites'\n" +
                "\n" +
                "                        WHEN 160034\n" +
                "                            THEN 'Died'\n" +
                "\n" +
                "                        ELSE 'Other'\n" +
                "                    END\n" +
                "            END\n" +
                "        ) AS Discontinuation_Reason,\n" +
                "\n" +
                "        DATE(e.encounter_datetime) AS date_discontinued,\n" +
                "\n" +
                "        COALESCE(\n" +
                "            MAX(\n" +
                "                CASE\n" +
                "                    WHEN o.concept_id = 164384\n" +
                "                    THEN DATE(o.value_datetime)\n" +
                "                END\n" +
                "            ),\n" +
                "\n" +
                "            MAX(\n" +
                "                CASE\n" +
                "                    WHEN o.concept_id = 159787\n" +
                "                    THEN DATE(o.value_datetime)\n" +
                "                END\n" +
                "            )\n" +
                "        ) AS effective_discontinuation_date\n" +
                "\n" +
                "    FROM qualified q\n" +
                "\n" +
                "    JOIN encounter e\n" +
                "        ON e.patient_id = q.patient_id\n" +
                "       AND e.voided = 0\n" +
                "        AND e.encounter_datetime >= :startDate\n" +
                "\n" +
                "    JOIN person p\n" +
                "        ON p.person_id = e.patient_id\n" +
                "       AND p.voided = 0\n" +
                "\n" +
                "    JOIN encounter_type et\n" +
                "        ON et.encounter_type_id = e.encounter_type\n" +
                "       AND et.uuid = '2bdada65-4c72-4a48-8730-859890e25cee'\n" +
                "\n" +
                "    JOIN obs o\n" +
                "        ON o.encounter_id = e.encounter_id\n" +
                "       AND o.voided = 0\n" +
                "       AND o.concept_id IN (\n" +
                "            161555,\n" +
                "            159786,\n" +
                "            159787,\n" +
                "            164384,\n" +
                "            1543,\n" +
                "            159495,\n" +
                "            160649,\n" +
                "            1285,\n" +
                "            164133,\n" +
                "            1599,\n" +
                "            1748,\n" +
                "            162580,\n" +
                "            160218,5240,164349,159492,167332\n" +
                "       )\n" +
                "\n" +
                "    JOIN concept c\n" +
                "        ON c.concept_id = o.concept_id\n" +
                "\n" +
                "    LEFT JOIN concept cv\n" +
                "        ON cv.concept_id = o.value_coded\n" +
                "\n" +
                "    GROUP BY\n" +
                "        e.patient_id,\n" +
                "        e.encounter_id,\n" +
                "        e.encounter_datetime\n" +
                "),\n" +
                "\n" +
                "dis_ranked AS (\n" +
                "    SELECT\n" +
                "        dg.*,\n" +
                "\n" +
                "        ROW_NUMBER() OVER (\n" +
                "            PARTITION BY dg.patient_id\n" +
                "            ORDER BY\n" +
                "                dg.encounter_datetime DESC,\n" +
                "                dg.encounter_id DESC\n" +
                "        ) AS rn\n" +
                "\n" +
                "    FROM dis_grouped dg\n" +
                "),\n" +
                "\n" +
                "dis AS (\n" +
                "    SELECT\n" +
                "        patient_id,\n" +
                "        Discontinuation_Reason,\n" +
                "        date_discontinued,\n" +
                "        effective_discontinuation_date\n" +
                "\n" +
                "    FROM dis_ranked\n" +
                "\n" +
                "    WHERE rn = 1\n" +
                "),\n" +
                "\n" +
                "tracing_grouped AS (\n" +
                "    SELECT\n" +
                "        e.patient_id,\n" +
                "        e.encounter_id,\n" +
                "\n" +
                "        MAX(o.obs_datetime) AS tracing_date,\n" +
                "        MAX(o.obs_id) AS latest_obs_id,\n" +
                "        MAX(e.date_created) AS date_traced,\n" +
                "\n" +
                "        MAX(\n" +
                "            CASE\n" +
                "                WHEN o.concept_id = '160433'\n" +
                "                THEN\n" +
                "                    CASE o.value_coded\n" +
                "\n" +
                "                        WHEN 160432\n" +
                "                            THEN 'Dead'\n" +
                "\n" +
                "                        WHEN 5240\n" +
                "                            THEN 'Lost to follow up'\n" +
                "\n" +
                "                        WHEN '164435'\n" +
                "                            THEN 'Stopped treatment'\n" +
                "\n" +
                "                        WHEN 1693\n" +
                "                            THEN 'Receiving ART from another clinic/Transferred'\n" +
                "\n" +
                "                        WHEN 160037\n" +
                "                            THEN 'Still in care at CCC'\n" +
                "\n" +
                "                        WHEN 142917\n" +
                "                            THEN 'Other'\n" +
                "\n" +
                "                        ELSE 'Unknown'\n" +
                "                    END\n" +
                "            END\n" +
                "        ) AS true_patient_status,\n" +
                "\n" +
                "        MAX(\n" +
                "            CASE\n" +
                "                WHEN o.concept_id = 164966\n" +
                "                THEN\n" +
                "                    CASE o.value_coded\n" +
                "\n" +
                "                        WHEN 164965\n" +
                "                            THEN 'Physical Tracing'\n" +
                "\n" +
                "                        WHEN 1650\n" +
                "                            THEN 'Client Called'\n" +
                "\n" +
                "                        WHEN 161642\n" +
                "                            THEN 'Treatment Supporter'\n" +
                "\n" +
                "                        ELSE 'Unknown'\n" +
                "                    END\n" +
                "            END\n" +
                "        ) AS tracing_type,\n" +
                "\n" +
                "        MAX(\n" +
                "            CASE\n" +
                "                WHEN o.concept_id = 1639\n" +
                "                THEN o.value_numeric\n" +
                "            END\n" +
                "        ) AS tracing_attempt_number,\n" +
                "\n" +
                "        MAX(s.username) AS username\n" +
                "\n" +
                "    FROM qualified q\n" +
                "\n" +
                "    JOIN encounter e\n" +
                "        ON e.patient_id = q.patient_id\n" +
                "       AND e.encounter_type = 31\n" +
                "       AND e.voided = 0\n" +
                "\n" +
                "    JOIN obs o\n" +
                "        ON o.encounter_id = e.encounter_id\n" +
                "       AND o.person_id = e.patient_id\n" +
                "       AND o.voided = 0\n" +
                "       AND o.date_created >= :startDate\n" +
                "\n" +
                "    JOIN users s\n" +
                "        ON s.user_id = e.creator\n" +
                "       AND o.creator = e.creator\n" +
                "\n" +
                "    JOIN concept c\n" +
                "        ON c.concept_id = o.concept_id\n" +
                "       AND o.concept_id IN (\n" +
                "            160433,\n" +
                "            164966,\n" +
                "            1639,\n" +
                "            160433\n" +
                "       )\n" +
                "\n" +
                "    LEFT JOIN concept ac\n" +
                "        ON ac.concept_id = o.value_coded\n" +
                "\n" +
                "    GROUP BY\n" +
                "        e.patient_id,\n" +
                "        e.encounter_id\n" +
                "),\n" +
                "\n" +
                "tracing_ranked AS (\n" +
                "    SELECT\n" +
                "        tg.*,\n" +
                "\n" +
                "        ROW_NUMBER() OVER (\n" +
                "            PARTITION BY tg.patient_id\n" +
                "            ORDER BY\n" +
                "                tg.tracing_date DESC,\n" +
                "                tg.encounter_id DESC,\n" +
                "                tg.latest_obs_id DESC\n" +
                "        ) AS rn\n" +
                "\n" +
                "    FROM tracing_grouped tg\n" +
                "),\n" +
                "\n" +
                "pt AS (\n" +
                "    SELECT\n" +
                "        patient_id,\n" +
                "        true_patient_status,\n" +
                "        tracing_type,\n" +
                "        tracing_attempt_number,\n" +
                "        date_traced,\n" +
                "        username\n" +
                "\n" +
                "    FROM tracing_ranked\n" +
                "\n" +
                "    WHERE rn = 1\n" +
                ")\n" +
                "\n" +
                "SELECT\n" +
                "    q.patient_id AS PID,\n" +
                "\n" +
                "    CONCAT_WS(\n" +
                "        ' ',\n" +
                "        pn.given_name,\n" +
                "        pn.family_name\n" +
                "    ) AS PATIENT_NAME,\n" +
                "\n" +
                "    pi.identifier AS IDENTIFIER,\n" +
                "\n" +
                "    q.contact AS 'CONTACT',\n" +
                "\n" +
                "    q.date_appointment_scheduled AS 'APPOINTMENT ISSUED',\n" +
                "\n" +
                "    q.start_date_time AS 'TCA DATE',\n" +
                "\n" +
                "    q.green_card_date AS 'GREEN CARD DATE',\n" +
                "\n" +
                "    q.date_dispensed AS 'DISPENSING DATE',\n" +
                "\n" +
                "    q.dispensed_quantity AS 'QTY DISPENSED',\n" +
                "\n" +
                "    DATEDIFF(\n" +
                "        :endDate,\n" +
                "        q.date_dispensed\n" +
                "    ) AS 'DISP DAYS ELAPSED',\n" +
                "\n" +
                "    (\n" +
                "        q.dispensed_quantity\n" +
                "        -\n" +
                "        DATEDIFF(\n" +
                "            :endDate,\n" +
                "            q.date_dispensed\n" +
                "        )\n" +
                "    ) AS 'TABLETS AT HAND',\n" +
                "\n" +
                "    DATEDIFF(\n" +
                "        q.start_date_time,\n" +
                "        q.date_appointment_scheduled\n" +
                "    ) AS 'TCA DAYS GIVEN',\n" +
                "\n" +
                "    DATEDIFF(\n" +
                "        :endDate,\n" +
                "        q.start_date_time\n" +
                "    ) AS 'DAYS MISSED',\n" +
                "\n" +
                "    CASE\n" +
                "\n" +
                "        WHEN DATEDIFF(\n" +
                "            q.green_card_date,\n" +
                "            q.start_date_time\n" +
                "        ) = 0\n" +
                "            THEN 'KEPT APP'\n" +
                "\n" +
                "        WHEN DATE(q.green_card_date) > DATE(q.date_appointment_scheduled)\n" +
                "         AND DATE(q.green_card_date) < DATE(q.start_date_time)\n" +
                "            THEN 'CAME EARLY/DOCUMENTATION GAP'\n" +
                "\n" +
                "        WHEN DATE(q.date_appointment_scheduled) >= DATE(q.green_card_date)\n" +
                "         AND DATEDIFF(:endDate, q.start_date_time) >= 30\n" +
                "            THEN 'LTFU'\n" +
                "\n" +
                "        WHEN DATE(q.date_appointment_scheduled) >= DATE(q.green_card_date)\n" +
                "         AND DATEDIFF(:endDate, q.start_date_time) < 30\n" +
                "            THEN 'MISSED'\n" +
                "\n" +
                "        ELSE 'CAME LATE/DOCUMENTATION GAP'\n" +
                "\n" +
                "    END AS 'STATUS',\n" +
                "    dis.date_discontinued AS 'DATE DISCONTINUED',\n" +
                "    dis.effective_discontinuation_date AS 'EFFECTIVE DISCONTINUATION DATE',        \n" +
                "    dis.Discontinuation_Reason AS 'DISCONTINUATION REASON',\n" +
                "\n" +
                "    pt.true_patient_status AS 'TRACING STATUS',\n" +
                "\n" +
                "    pt.tracing_type AS 'TRACING TYPE',\n" +
                "\n" +
                "    pt.tracing_attempt_number AS 'TRACING ATTEMPTS',\n" +
                "\n" +
                "    pt.date_traced AS 'TRACING DATE',\n" +
                "\n" +
                "    pt.username AS 'TRACED BY'\n" +
                "\n" +
                "FROM qualified q\n" +
                "\n" +
                "JOIN person_name pn\n" +
                "    ON pn.person_id = q.patient_id\n" +
                "   AND pn.preferred = 1\n" +
                "   AND pn.voided = 0\n" +
                "\n" +
                "JOIN patient_identifier pi\n" +
                "    ON pi.patient_id = q.patient_id\n" +
                "   AND pi.identifier_type = 6\n" +
                "   AND pi.voided = 0\n" +
                "\n" +
                "LEFT JOIN dis\n" +
                "    ON dis.patient_id = q.patient_id\n" +
                "\n" +
                "LEFT JOIN pt\n" +
                "    ON pt.patient_id = q.patient_id\n" +
                "\n" +
                "\n" +
                "\n" +
                "ORDER BY\n" +
                "    q.start_date_time DESC,\n" +
                "    q.patient_id;");

        return dsd;

    }

}