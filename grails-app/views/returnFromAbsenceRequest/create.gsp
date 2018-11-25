<!DOCTYPE html>

<html>
<head>
    <meta name="layout" content="mainGUI" />
    <g:set var="entity" value="${message(code: 'returnFromAbsenceRequest.entity', default: 'ReturnFromAbsenceRequest List')}" />
    <g:set var="title" value="${message(code: 'default.create.label',args:[entity], default: 'ReturnFromAbsenceRequest List')}" />
    <title>${title}</title>
</head>
<body>
<lay:widget title="${title}">
    <lay:widgetToolBar>
        <btn:buttonGroup>
            <btn:listButton onClick="window.location.href='${createLink(controller:'returnFromAbsenceRequest',action:'list')}'"/>
        </btn:buttonGroup>
    </lay:widgetToolBar>
    <lay:widgetBody>
        <el:row>
            <msg:page />
            <el:validatableResetForm name="returnFromAbsenceRequestForm" callBackFunction="successCallBack" controller="returnFromAbsenceRequest" action="selectAbsence">



                <g:render template="/employee/wrapper" model="[isDisabled            : false,
                                                               name                  : 'employeeId',
                                                               id                    : 'employeeId',
                                                               paramsGenerateFunction: 'employeeParams',
                                                               size                  : 6]"/>

                <el:formGroup>
                    <el:autocomplete optionKey="id" paramsGenerateFunction="absenceParam" optionValue="info" size="6"
                                     class=" isRequired" controller="absence" action="autocomplete"
                                     name="absenceId"
                                     id="absenceId"
                                     label="${message(code:'absence.label',default:'absence')}"
                                     values="${[[returnFromAbsenceRequest?.absence?.id,returnFromAbsenceRequest?.absence?.descriptionInfo?.localName]]}" />
                </el:formGroup>


                <el:formButton functionName="select" withClose="true" isSubmit="true"/>
                <el:formButton functionName="cancel" withPreviousLink="true" goToPreviousLink="true"/>

            </el:validatableResetForm>
        </el:row>
    </lay:widgetBody>
</lay:widget>
<script>

    function successCallBack(json) {
        if (json.success) {
            window.location.href = "${createLink(controller: 'returnFromAbsenceRequest',action: 'createNewRequest')}?absenceId=" + json.absenceId;
        }
    }

    function absenceParam() {
        return {'excludedStatusList':'RETURNED,CLOSED', 'employee.id':$("#employeeId").val()}
    }

    function employeeParams() {
        var searchParams = {};
        searchParams.categoryStatusId = "${ps.gov.epsilon.hr.enums.profile.v1.EnumEmployeeStatusCategory.COMMITTED.toString()}";
        return searchParams;
    }

    $("#employeeId").on("select2:close", function (e) {
        var value = $('#employeeId').val();
        if(value){
            $('#absenceId').val("");
            $('#absenceId').trigger('change');
        }
    });

    $("#absenceId").on("select2:close", function (e) {
        var value = $('#absenceId').val();
        var employeeIdValue = $('#employeeId').val();
        if(value && !employeeIdValue){
            $.ajax({
                url: '${createLink(controller: 'absence',action: 'getInstance')}',
                type: 'POST',
                data: {
                    id: value
                },
                dataType: 'json',
                beforeSend: function(jqXHR,settings) {
                    guiLoading.show();
                },
                error: function(jqXHR) {
                    guiLoading.hide();
                },
                success: function(json) {
                    guiLoading.hide();
                    if(json.employeeId){
                        $("#employeeId").val(json.employeeId);
                        var newOption = new Option(json.employeeName,json.employeeId, true, true);
                        $('#employeeId').append(newOption);
                        $('#employeeId').trigger('change');
                    }
                }
            });
        }
    });

</script>
</body>
</html>
