<!DOCTYPE html>

<html>
<head>
    <meta name="layout" content="mainGUI" />
    <g:set var="entity" value="${message(code: 'promotionRequest.entity', default: 'promotionRequest List')}" />
    <g:set var="title" value="${message(code: 'default.create.label',args:[entity], default: 'promotionRequest List')}" />
    <title>${title}</title>
</head>
<body>
<script>
    function successCallBack(json) {
        if (json.success) {
            window.location.href = "${createLink(controller: 'promotionRequest',action: 'createNewRequest')}?employeeId=" + json.employeeId + "&requestType="+json.requestType;
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
                    onClick="window.location.href='${createLink(controller: 'promotionRequest', action: 'list')}'"/>
        </btn:buttonGroup>
    </lay:widgetToolBar>
    <lay:widgetBody>
        <el:row>
            <msg:warning label="${message(code:'request.justCommittedEmployee.label')}" />
            <msg:page/>
            <el:validatableResetForm name="promotionRequestForm" callBackFunction="successCallBack"
                                     controller="promotionRequest" action="selectEmployee">

                <g:render template="/employee/wrapper" model="[isDisabled            : false,
                                                               name                  : 'employeeId',
                                                               id                    : 'employeeId',
                                                               paramsGenerateFunction: 'employeeParams',
                                                               size                  :  6]"/>

                <el:formGroup>
                    <el:select valueMessagePrefix="EnumRequestType"
                               from="${enumRequestTypeList}"
                               name="requestType" size="6" class=" isRequired"
                               label="${message(code: 'promotionRequest.requestType.label', default: 'requestType')}"
                               value="${updateMilitaryRankRequest?.requestType}"/>

                </el:formGroup>
                <el:row/>

                <el:formButton functionName="select" withClose="true" isSubmit="true"/>
                <el:formButton functionName="cancel" withPreviousLink="true" goToPreviousLink="true"/>
            </el:validatableResetForm>
        </el:row>
    </lay:widgetBody>
</lay:widget>
</body>
</html>
