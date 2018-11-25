<!DOCTYPE html>

<html>
<head>
    <meta name="layout" content="mainGUI" />
    <g:set var="entity" value="${message(code: 'petitionRequest.entity', default: 'PetitionRequest List')}" />
    <g:set var="title" value="${message(code: 'default.create.label',args:[entity], default: 'PetitionRequest List')}" />
    <title>${title}</title>
</head>
<body>
<lay:widget title="${title}">
    <lay:widgetToolBar>
        <btn:buttonGroup>
            <btn:listButton onClick="window.location.href='${createLink(controller:'petitionRequest',action:'list')}'"/>
        </btn:buttonGroup>
    </lay:widgetToolBar>
    %{--<lay:widgetBody>--}%
        %{--<el:row>--}%
            %{--<msg:page />--}%
            %{--<el:validatableResetForm name="petitionRequestForm" callBackFunction="successCallBack" controller="petitionRequest" action="selectRequest">--}%
                %{--<el:formGroup>--}%
                    %{--<el:autocomplete optionKey="id" paramsGenerateFunction="disciplinaryRequestParam" optionValue="info" size="6" class=" isRequired" controller="disciplinaryRequest" action="autocomplete" name="disciplinaryRequestId" label="${message(code:'disciplinaryRequest.label',default:'disciplinaryRequest')}" values="${[[petitionRequest?.disciplinaryRequest?.id,petitionRequest?.disciplinaryRequest?.descriptionInfo?.localName]]}" />--}%
                %{--</el:formGroup>--}%
                %{--<el:formButton functionName="select" withClose="true" isSubmit="true"/>--}%
                %{--<el:formButton functionName="cancel" withPreviousLink="true" goToPreviousLink="true"/>--}%
            %{--</el:validatableResetForm>--}%
        %{--</el:row>--}%
    %{--</lay:widgetBody>--}%


    <lay:widgetBody>
        <el:row>
            <msg:page />
            <el:validatableResetForm name="petitionRequestForm" callBackFunction="successCallBack" controller="petitionRequest" action="selectRequest">
                <g:render template="/employee/wrapper" model="[isDisabled            : false,
                                                               name                  : 'employeeId',
                                                               id                    : 'employeeId',
                                                               paramsGenerateFunction: 'employeeParams',
                                                               size                  : 6]"/>

                <el:formGroup>
                    <el:autocomplete optionKey="id" paramsGenerateFunction="disciplinaryRequestParam" optionValue="info" size="6"
                                     class=" isRequired" controller="disciplinaryRequest" action="autocomplete"
                                     name="disciplinaryRequestId"
                                     id="disciplinaryRequestId"
                                     label="${message(code:'disciplinaryRequest.label',default:'disciplinaryRequest')}"
                                     values="${[[petitionRequest?.disciplinaryRequest?.id,petitionRequest?.disciplinaryRequest?.descriptionInfo?.localName]]}" />
                </el:formGroup>

                <el:formButton functionName="select" withClose="true" isSubmit="true"/>
                <el:formButton functionName="cancel" withPreviousLink="true" goToPreviousLink="true"/>
            </el:validatableResetForm>
        </el:row>
    </lay:widgetBody>



</lay:widget>
<script>
    function disciplinaryRequestParam() {
        return {'requestStatus':'${ps.gov.epsilon.hr.enums.v1.EnumRequestStatus.APPROVED}', 'employee.id':$("#employeeId").val()}
    }

    function successCallBack(json) {
        if (json.success) {
            window.location.href = "${createLink(controller: 'petitionRequest',action: 'createNewRequest')}?disciplinaryRequestId=" + json.disciplinaryRequestId;
        }
    }

    function employeeParams() {
        var searchParams = {};
        searchParams.categoryStatusId = "${ps.gov.epsilon.hr.enums.profile.v1.EnumEmployeeStatusCategory.COMMITTED.toString()}";
        return searchParams;
    }

    $("#employeeId").on("select2:close", function (e) {
        var value = $('#employeeId').val();
        if(value){
            $('#disciplinaryRequestId').val("");
            $('#disciplinaryRequestId').trigger('change');
        }
    });

    $("#disciplinaryRequestId").on("select2:close", function (e) {
        var value = $('#disciplinaryRequestId').val();
        var employeeIdValue = $('#employeeId').val();
        if(value && !employeeIdValue){
            $.ajax({
                url: '${createLink(controller: 'disciplinaryRequest',action: 'getInstance')}',
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
