<lay:widget transparent="true" color="blue" icon="icon-info-4" title="${g.message(code: "loanNotice.info.label")}">
    <lay:widgetBody>

        <msg:warning label="${message(code:'loanNotice.loanRequestWarning.label')}" />


        <el:formGroup>
            <el:autocomplete optionKey="id" optionValue="name" size="6" class=" isRequired" controller="job"
                             action="autocomplete" name="requestedJob.id"
                             label="${message(code:'loanNotice.requestedJob.label',default:'requestedJob')}"
                             values="${[[loanNotice?.requestedJob?.id,loanNotice?.requestedJob?.descriptionInfo?.localName]]}" />

            <el:textField name="jobTitle" size="6"  class=""
                          label="${message(code:'loanNotice.jobTitle.label',default:'jobTitle')}"
                          value="${loanNotice?.jobTitle}"/>
        </el:formGroup>



        <el:formGroup>

            <el:autocomplete optionKey="id" optionValue="name" size="6" class=" isRequired"
                             paramsGenerateFunction="organizationParams"
                             controller="organization" action="autocomplete"
                             name="requesterOrganizationId"
                             label="${message(code:'loanNotice.requesterOrganizationId.label',default:'requesterOrganizationId')}"
                             values="${[[loanNotice?.requesterOrganizationId,
                                         loanNotice?.transientData?.requesterOrganizationDTO?.toString()]]}" />

            <el:integerField name="numberOfPositions" size="6" maxlength="4"
                             class=" isRequired isNumber"
                             label="${message(code:'loanNotice.numberOfPositions.label',default:'numberOfPositions')}"
                             value="${loanNotice?.numberOfPositions}" />
        </el:formGroup>
        
        

        <el:formGroup>
            <el:dateField name="fromDate"  size="6" class=" isRequired" 
                          label="${message(code:'loanNotice.fromDate.label',default:'fromDate')}" 
                          value="${loanNotice?.fromDate}" />

            <el:dateField name="toDate"  size="6" class=" isRequired" 
                          label="${message(code:'loanNotice.toDate.label',default:'toDate')}" 
                          value="${loanNotice?.toDate}" />
        </el:formGroup>

        <el:formGroup>
            <el:integerField isReadOnly="true" name="periodInMonths" size="6"  class=" isRequired isNumber"
                             label="${message(code:'loanNotice.periodInMonths.label',default:'periodInMonths')}"
                             value="${loanNotice?.periodInMonths}" />

            <el:textArea name="description" size="6"  class=""
                         label="${message(code:'loanNotice.description.label',default:'description')}"
                         value="${loanNotice?.description}"/>
        </el:formGroup>
        
        
    </lay:widgetBody>
</lay:widget>





<script type="text/javascript">

    function sendFirmData(){
        return {"firm.id":"${ps.police.common.utils.v1.PCPSessionUtils.getValue("firmId")}"};
    }


    function organizationParams() {
        return {
            "organizationType.id":"${ps.police.pcore.enums.v1.OrganizationTypeEnum.SECURITY_FORCES.value()}"
        };
    }

    /*on change the toDate, calculate the  difference in months between fromDate, toDate and if difference is greater than 1 year set the nextVerificationDate value*/
    $("#fromDate").change(function () {
        setPeriodValue();
    });
    $("#toDate").change(function () {
        setPeriodValue();
    });

    function setPeriodValue(){
        var fromDate = $("#fromDate").val();
        var toDate = $("#toDate").val();
        if (toDate && fromDate) {
            var parts1 = fromDate.split("/");
            var date1 = new Date(parseInt(parts1[2], 10),
                parseInt(parts1[1], 10) - 1,
                parseInt(parts1[0], 10));

            var parts2 = toDate.split("/");
            var date2 = new Date(parseInt(parts2[2], 10),
                parseInt(parts2[1], 10) - 1,
                parseInt(parts2[0], 10));
            var diffInMonths = monthDiff(date1, date2);
            $("#periodInMonths").val(diffInMonths);
        }
    }

    /*calculate the months different between tow dates*/
    function monthDiff(d1, d2) {
        var months;
        months = (d2.getFullYear() - d1.getFullYear()) * 12;
        months -= d1.getMonth();
        months += d2.getMonth();
        return months <= 0 ? 0 : months;
    }


</script>