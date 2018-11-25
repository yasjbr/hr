<!DOCTYPE html>
<html>
<head>
    <meta name="layout" content="mainGUI"/>
    <g:set var="entity" value="${message(code: 'recruitmentCycle.entity', default: 'RecruitmentCycle List')}"/>
    <g:set var="title"
           value="${message(code: 'recruitmentCycle.change.phase.label')}"/>
    <title>${title}</title>

</head>

<body>
<lay:widget title="${title}">
    <lay:widgetToolBar>
        <btn:buttonGroup>
            <btn:listButton
                    onClick="window.location.href='${createLink(controller: 'recruitmentCycle', action: 'list')}'"/>
        </btn:buttonGroup>
    </lay:widgetToolBar>
    <lay:widgetBody>
        <msg:page/>
        <lay:showWidget size="6" title="">
            <lay:showElement value="${recruitmentCycle?.name}" type="String"
                             label="${message(code: 'recruitmentCycle.name.label', default: 'name')}"/>
            <lay:showElement value="${recruitmentCycle?.currentRecruitmentCyclePhase?.fromDate}" type="ZonedDate"
                             label="${message(code: 'recruitmentCycle.currentRecruitmentCyclePhase.fromDate.label', default: 'from Date')}"/>
        </lay:showWidget>
        <lay:showWidget size="6" title="">

            <lay:showElement value="${recruitmentCycle?.currentRecruitmentCyclePhase?.requisitionAnnouncementStatus}"
                             type="enum"
                             label="${message(code: 'recruitmentCycle.currentRecruitmentCyclePhase.label', default: 'requisitionAnnouncementStatus')}"
                             messagePrefix="EnumRequisitionAnnouncementStatus"/>
            <lay:showElement value="${recruitmentCycle?.currentRecruitmentCyclePhase?.toDate}" type="ZonedDate"
                             label="${message(code: 'recruitmentCycle.currentRecruitmentCyclePhase.toDate.label', default: 'to Date')}"/>
        </lay:showWidget>
        <el:row/>
        <el:row>

            <el:validatableForm name="recruitmentCycleForm" controller="recruitmentCycle" action="update">
                <el:hiddenField name="encodedId" value="${recruitmentCycle?.encodedId}"/>
                <g:render template="/recruitmentCycle/changePhase" model="[recruitmentCycle: recruitmentCycle]"/>
                <el:row/>
                <el:formButton functionName="save" onClick="confirmSave()" withPreviousLink="true"/>
                <el:formButton functionName="cancel"  goToPreviousLink="true"/>
            </el:validatableForm>
        </el:row>
    </lay:widgetBody>
</lay:widget>


<script>

    //validate the dates of cycle and show warning in case its still open
    function confirmSave() {
        $('.alert.page').html("");
        var newFromDate = 0;
        var newToDate = 0;
        if ($("#fromDate").val()) {
            newFromDate = $("#fromDate").datepicker("getDate").getTime();

            //1- the new to date should be lager than from date
            if ($("#toDate").val()) {
                newToDate = $("#toDate").datepicker("getDate").getTime();
                if (newToDate < newFromDate) {
                    gui.formValidatable.showErrorMessage("${message(code:'recruitmentCyclePhase.toDate.value.warning')}");
                    return
                }
            }

            //current phase (toDate)
            var oldToDate = '${formattedToDate}';
            var oldFromDate = '${formattedFromDate}';

            //2- oldToDate is empty, and user enter new from date less than old from date:
            if (newFromDate < oldFromDate) {
                gui.formValidatable.showErrorMessage("${message(code:'recruitmentCyclePhase.fromDate.value.wrong')}");
                return
            } else {
                //this warning message is passed from controller:
                var warningMessage = "${warningMessage}";

                //check if the current phase (toDate) will be ended before the start of new phase (fromDate), if not warn user
                if (newFromDate < oldToDate) {
                    if (warningMessage != "") {
                        warningMessage += "<br/>";
                    }
                    warningMessage += "${message(code: 'recruitmentCycle.changePhase.confirm.message', 'default': 'You can not edit the answers when you save. Are you sure you want to complete the save?')}";
                }

                if (warningMessage != "") {
                    gui.confirm.confirmFunc("${message(code: 'recruitmentCycle.changePhase.confirm', 'default': 'Confirm Save')}", warningMessage, function () {
                        $("#recruitmentCycleForm").submit();
                    });
                } else {
                    $("#recruitmentCycleForm").submit();
                }
            }

        } else {
            gui.formValidatable.showErrorMessage("${message(code:'recruitmentCyclePhase.fromDate.value.warning')}");
        }
    }
</script>

</body>
</html>