<!DOCTYPE html>

<html>
<head>
    <meta name="layout" content="mainGUI"/>
    <g:set var="entity"
           value="${message(code: 'endOfService.entity', default: 'endOfService List')}"/>
    <g:set var="title"
           value="${message(code: 'default.create.label', args: [entity], default: 'endOfService List')}"/>
    <title>${title}</title>
</head>

<body>
<script>
    function successCallBack(json) {
        if (json.success) {
            window.location.href = "${createLink(controller: 'employmentServiceRequest',action: 'createEndOfService')}?employeeId=" + json.employeeId;
        }
    }
    function employeeParams() {
        return {'categoryStatusId':'${ps.gov.epsilon.hr.enums.profile.v1.EnumEmployeeStatusCategory.COMMITTED.value}'}
    }
</script>
<lay:widget title="${title}">
    <lay:widgetToolBar>
        <btn:buttonGroup>
            <btn:listButton
                    onClick="window.location.href='${createLink(controller: 'employmentServiceRequest', action: 'listEndOfService')}'"/>
        </btn:buttonGroup>
    </lay:widgetToolBar>
    <lay:widgetBody>
        <el:row>
            <msg:page/>
            <el:validatableResetForm name="employmentServiceRequestForm"
                                     callBackFunction="successCallBack"
                                     controller="employmentServiceRequest"
                                     action="selectEmployeeEndOfService">

                <g:render template="/employee/wrapper" model="[isDisabled            : false,
                                                               name                  : 'employeeId',
                                                               id                    : 'employeeId',
                                                               paramsGenerateFunction: 'employeeParams',
                                                               size                  :  6]"/>

                <el:formButton functionName="select" withClose="true" isSubmit="true"/>
                <el:formButton functionName="cancel" withPreviousLink="true" goToPreviousLink="true"/>
            </el:validatableResetForm>
        </el:row>
    </lay:widgetBody>
</lay:widget>
</body>
</html>