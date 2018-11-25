<el:hiddenField name="employee.id" value="${absence?.employee?.id}"/>
<g:render template="/employee/wrapperForm" model="[employee:absence?.employee]"  />
<br/>

<lay:widget transparent="true" color="blue" icon="icon-info-4"
            title="${g.message(code: "absence.info.label")}">
    <lay:widgetBody>
        <br/>
        <el:formGroup>
            <el:select valueMessagePrefix="EnumAbsenceReason"
                       from="${ps.gov.epsilon.hr.enums.absence.v1.EnumAbsenceReason.values()}" name="absenceReason"
                       size="6"
                       class=" isRequired"
                       label="${message(code: 'absence.absenceReason.label', default: 'absenceReason')}"
                       value="${absence?.absenceReason}"/>

            %{--<el:integerField id="numOfDays" name="numOfDays" size="6" class=" isNumber"--}%
                             %{--label="${message(code: 'absence.numOfDays.label', default: 'numOfDays')}"--}%
                             %{--value="${absence?.numOfDays}"/>--}%

        %{--</el:formGroup>--}%

        %{--<el:formGroup>--}%
            <el:dateField isMaxDate="true" id="fromDate" name="fromDate" size="6" class=" isRequired" setMinDateFor="toDate"
                          label="${message(code: 'absence.fromDate.label', default: 'fromDate')}"
                          value="${absence?.fromDate}"/>
            %{--<el:dateField isDisabled="true" isMaxDate="true" name="toDate" id="toDate" size="6" class=""--}%
                          %{--label="${message(code: 'absence.toDate.label', default: 'toDate')}"--}%
                          %{--value="${absence?.toDate}"/>--}%
        </el:formGroup>

        <el:formGroup>
            <el:dateField isMaxDate="true" name="noticeDate" size="6" class=" isRequired"
                          label="${message(code: 'absence.noticeDate.label', default: 'noticeDate')}"
                          value="${absence?.noticeDate}"/>
            <el:autocomplete optionKey="id"
                             optionValue="name"
                             size="6"
                             class=""
                             controller="employee"
                             action="autocomplete"
                             name="informer.id"
                             paramsGenerateFunction="employeeParams"
                             label="${message(code: 'absence.informer.label', default: 'informer')}"
                             values="${[[absence?.informer?.id, absence?.informer]]}"/>
        </el:formGroup>

        <el:formGroup>
            <el:textArea name="reasonDescription" size="6" class=""
                         label="${message(code: 'absence.reasonDescription.label', default: 'reasonDescription')}"
                         value="${absence?.reasonDescription}"/>
        </el:formGroup>
    </lay:widgetBody>
</lay:widget>
<br/>

<script type="text/javascript">

    function employeeParams() {
        return {'idsToExclude[]':'${absence?.employee?.id}'}
    }

    $("#toDate").change(function () {
        var toDate = $(this).val();
        var fromDate = $("#fromDate").val();
        if (toDate && fromDate) {
            var parts1 = fromDate.split("/");
            var d1 = new Date(parseInt(parts1[2], 10),
                parseInt(parts1[1], 10) - 1,
                parseInt(parts1[0], 10));
            var parts2 = toDate.split("/");
            var d2 = new Date(parseInt(parts2[2], 10),
                parseInt(parts2[1], 10) - 1,
                parseInt(parts2[0], 10));
            var oneDay = 24 * 60 * 60 * 1000; /*hours*minutes*seconds*milliseconds*/
            var diffDays = Math.abs((d2.getTime() - d1.getTime()) / (oneDay));
            $("#numOfDays").val(Math.round(diffDays) + 1);
        }
    });
</script>

