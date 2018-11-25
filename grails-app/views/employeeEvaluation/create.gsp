<!DOCTYPE html>

<html>
<head>
    <meta name="layout" content="mainGUI" />
    <g:set var="entity" value="${message(code: 'employeeEvaluation.entity', default: 'employeeEvaluation List')}" />
    <g:set var="title" value="${message(code: 'default.create.label',args:[entity], default: 'employeeEvaluation List')}" />
    <title>${title}</title>
</head>
<body>
<script>
    function successCallBack(json) {
        if (json.success) {
            window.location.href = "${createLink(controller: 'employeeEvaluation',action: 'createNewEmployeeEvaluation')}?employeeId=" + json.employeeId + "&evaluationTemplateId="+json.evaluationTemplateId;
        }
    }
    function employeeParams() {
        return {'categoryStatusId':'${ps.gov.epsilon.hr.enums.profile.v1.EnumEmployeeStatusCategory.COMMITTED.value}'}
    }
    function templateParams() {
        return {
            'employeeId':$("#employeeId").val(),
            'templateType':$("#templateType").val(),
            'isUniqueEvaluationTemplate':true
        }

    }
</script>
<lay:widget title="${title}">
    <lay:widgetToolBar>
        <btn:buttonGroup>
            <btn:listButton
                    onClick="window.location.href='${createLink(controller: 'employeeEvaluation', action: 'list')}'"/>
        </btn:buttonGroup>
    </lay:widgetToolBar>
    <lay:widgetBody>
        <el:row>
            <msg:warning label="${message(code:'request.justCommittedEmployee.label')}" />
            <msg:warning label="${message(code:'employeeEvaluation.evaluationTemplate.select.label')}" />
            <msg:page/>
            <el:validatableResetForm name="employeeEvaluationForm" callBackFunction="successCallBack"
                                     controller="employeeEvaluation" action="selectEmployee">

                <g:render template="/employee/wrapper" model="[isDisabled            : false,
                                                               name                  : 'employeeId',
                                                               id                    : 'employeeId',
                                                               paramsGenerateFunction: 'employeeParams',
                                                               size                  :  6]"/>

                <el:formGroup>
                    <el:select valueMessagePrefix="EnumEvaluationTemplateType"
                               from="${ps.gov.epsilon.hr.enums.evaluation.v1.EnumEvaluationTemplateType.values()}"
                               name="templateType" size="6"  class=" isRequired"
                        id="templateType"
                               label="${message(code:'evaluationTemplate.templateType.label',default:'templateType')}"
                               value="${evaluationTemplate?.templateType}" />
                </el:formGroup>

                <el:formGroup>
                    <el:autocomplete label="${message(code:'employeeEvaluation.evaluationTemplate.label', default: 'evaluationTemplate')}"
                                     name="evaluationTemplate.id"
                                     optionKey="id"
                                     optionValue="name"
                                     size="6"
                                     class=" isRequired"
                                     paramsGenerateFunction="templateParams"
                                     controller="joinedEvaluationTemplateCategory"
                                     action="autocomplete" />
                </el:formGroup>

                <el:formButton functionName="select" withClose="true" isSubmit="true"/>
                <el:formButton functionName="cancel" withPreviousLink="true" goToPreviousLink="true"/>
            </el:validatableResetForm>
        </el:row>
    </lay:widgetBody>
</lay:widget>
</body>
</html>
