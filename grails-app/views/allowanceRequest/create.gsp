<!DOCTYPE html>

<html>
<head>
    <meta name="layout" content="mainGUI"/>
    <g:set var="entity" value="${message(code: 'allowanceRequest.entity', default: 'allowanceRequest List')}"/>
    <g:set var="title"
           value="${message(code: 'default.create.label', args: [entity], default: 'allowanceRequest List')}"/>
    <title>${title}</title>

    <script>
        function successCallBack(json) {
            if (json.success) {
                window.location.href = "${createLink(controller: 'allowanceRequest',action: 'createNewAllowanceRequest')}?employeeId=" + json.employeeId + "&allowanceTypeId=" + json.allowanceTypeId;
            }
        }
    </script>
</head>

<body>

<lay:widget title="${title}">
    <lay:widgetToolBar>
        <btn:buttonGroup>
            <btn:listButton
                    onClick="window.location.href='${createLink(controller: 'allowanceRequest', action: 'list')}'"/>
        </btn:buttonGroup>
    </lay:widgetToolBar>
    <lay:widgetBody>
        <el:row>
            <msg:page/>
            <el:validatableResetForm callBackFunction="successCallBack" name="allowanceRequestForm"
                                     controller="allowanceRequest" action="getEmployee">

                <msg:warning label="${message(code:'request.justCommittedEmployee.label')}" />
                <el:formGroup>
                    <el:autocomplete optionKey="id" optionValue="name" size="6" class=" isRequired"
                                     controller="allowanceType"
                                     action="autocomplete" name="allowanceType.id"
                                     label="${message(code: 'allowanceRequest.allowanceType.label', default: 'allowanceType')}"/>
                </el:formGroup>


                <g:render template="/employee/wrapper" model="[isDisabled            : false,
                                                               name                  : 'employeeId',
                                                               id                    : 'employeeId',
                                                               paramsGenerateFunction: 'employeeParams',
                                                               size                  : 6]"/>

                <el:formButton functionName="select" isSubmit="true"/>
                <el:formButton functionName="cancel" withPreviousLink="true" goToPreviousLink="true"/>
            </el:validatableResetForm>
        </el:row>
    </lay:widgetBody>
</lay:widget>

<script>

    /**
     * to get only employee with status COMMITTED
     */
    function employeeParams() {
        var searchParams = {};
        searchParams.categoryStatusId = "${ps.gov.epsilon.hr.enums.profile.v1.EnumEmployeeStatusCategory.COMMITTED.toString()}";
        return searchParams;
    }

</script>

</body>
</html>