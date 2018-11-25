<!DOCTYPE html>

<html>
<head>
    <meta name="layout" content="mainGUI"/>
    <g:set var="entity" value="${message(code: 'maritalStatusRequest.entity', default: 'MaritalStatusRequest List')}"/>
    <g:set var="title"
           value="${message(code: 'default.create.label', args: [entity], default: 'MaritalStatusRequest List')}"/>
    <title>${title}</title>
    <g:render template="script" />
</head>

<body>
<lay:widget title="${title}">
    <lay:widgetToolBar>
        <btn:buttonGroup>
            <btn:listButton
                    onClick="window.location.href='${createLink(controller: 'maritalStatusRequest', action: 'list')}'"/>
        </btn:buttonGroup>
    </lay:widgetToolBar>
    <lay:widgetBody>
        <el:row>
            <msg:warning label="${message(code:'request.justCommittedEmployee.label')}" />
            <msg:page/>
            <el:validatableResetForm name="maritalStatusRequestForm" callBackFunction="successCallBack"
                                     controller="maritalStatusRequest" action="selectEmployee">

                <g:render template="/employee/wrapper" model="[isDisabled            : false,
                                                               name                  : 'employeeId',
                                                               id                    : 'employeeId',
                                                               paramsGenerateFunction: 'employeeParams',
                                                               size                  :  6]"/>

                <el:formButton functionName="select" withClose="true" isSubmit="true"/>
                <el:formButton functionName="cancel" goToPreviousLink="true"/>
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

    %{-- used in create page to redirect the save into other action --}%
    function successCallBack(json) {
        if (json.success) {
            window.location.href = "${createLink(controller: 'maritalStatusRequest',action: 'createNewRequest')}?employeeId=" + json.employeeId;
        }
    }

</script>
</body>
</html>
