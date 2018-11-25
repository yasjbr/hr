<!DOCTYPE html>

<html>
<head>
    <meta name="layout" content="mainGUI"/>
    <g:set var="entity"
           value="${message(code: 'bordersSecurityCoordination.entity', default: 'BordersSecurityCoordination List')}"/>
    <g:set var="title"
           value="${message(code: 'default.create.label', args: [entity], default: 'BordersSecurityCoordination List')}"/>
    <title>${title}</title>

    <script>
        function employeeParams() {
            return {'categoryStatusId':'${ps.gov.epsilon.hr.enums.profile.v1.EnumEmployeeStatusCategory.COMMITTED.value}'}
        }
        function successCallBack(json) {
            if (json.success) {
                window.location.href = "${createLink(controller: 'bordersSecurityCoordination',action: 'createNewBordersSecurityCoordination')}?employeeId=" + json.employeeId;
            }
        }
    </script>

</head>

<body>
<lay:widget title="${title}">
    <lay:widgetToolBar>
        <btn:buttonGroup>
            <btn:listButton
                    onClick="window.location.href='${createLink(controller: 'bordersSecurityCoordination', action: 'list')}'"/>
        </btn:buttonGroup>
    </lay:widgetToolBar>
    <lay:widgetBody>
        <el:row>
            <msg:warning label="${message(code:'request.justCommittedEmployee.label')}" />
            <msg:page/>
            <el:validatableResetForm name="bordersSecurityCoordinationForm" callBackFunction="successCallBack"
                                     callLoadingFunction="performPostActionWithEncodedId"
                                     controller="bordersSecurityCoordination" action="selectEmployee">
                <el:formGroup>

                    <g:render template="/employee/wrapper" model="[isDisabled            : false,
                                                                   name                  : 'employeeId',
                                                                   id                    : 'employeeId',
                                                                   paramsGenerateFunction: 'employeeParams',
                                                                   size                  :  6]"/>
                </el:formGroup>
                <el:formButton functionName="select" isSubmit="true"/>
                <el:formButton functionName="cancel"  goToPreviousLink="true"/>
            </el:validatableResetForm>
        </el:row>
    </lay:widgetBody>
</lay:widget>
</body>
</html>
