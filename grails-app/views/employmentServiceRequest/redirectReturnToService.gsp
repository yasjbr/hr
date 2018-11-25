<!DOCTYPE html>

<html>
<head>
    <meta name="layout" content="mainGUI"/>
    <g:set var="entity" value="${message(code: 'recallToService.entity', default: 'recallToService List')}"/>
    <g:set var="title"
           value="${message(code: 'default.create.label', args: [entity], default: 'recallToService List')}"/>
    <title>${title}</title>
</head>

<body>
<script>
    function successCallBack(json) {
        if (json.success) {
            window.location.href = "${createLink(controller: 'employmentServiceRequest',action: 'createReturnToService')}?employeeId=" + json.employeeId + "&serviceActionReasonId=" +json.serviceActionReasonId;
        }
    }
    function employeeParams() {
        return {'categoryStatusId':'${ps.gov.epsilon.hr.enums.profile.v1.EnumEmployeeStatusCategory.UNCOMMITTED.value}', 'allowReturnToService':true}
    }
</script>
<lay:widget title="${title}">
    <lay:widgetToolBar>
        <btn:buttonGroup>
            <btn:listButton
                    onClick="window.location.href='${createLink(controller: 'employmentServiceRequest', action: 'listReturnToService')}'"/>
        </btn:buttonGroup>
    </lay:widgetToolBar>
    <lay:widgetBody>
        <el:row>
            <msg:warning label="${message(code:'recallToService.unCommittedEmployee.label')}" />

            <msg:page/>
            <el:validatableResetForm name="employmentServiceRequestForm"
                                     callBackFunction="successCallBack"
                                     controller="employmentServiceRequest"
                                     action="selectEmployeeReturnToService">
                <g:render template="/employee/wrapper" model="[isDisabled            : false,
                                                               name                  : 'employeeId',
                                                               id                    : 'employeeId',
                                                               paramsGenerateFunction: 'employeeParams',
                                                               size                  :  6]"/>

                <el:formGroup>
                    %{--<el:select--}%
                            %{--valueMessagePrefix="EnumServiceActionReason"--}%
                            %{--from="${ps.gov.epsilon.hr.enums.employmentService.v1.EnumServiceActionReason.values().key}"--}%
                            %{--name="serviceActionReason"--}%
                            %{--size="6"--}%
                            %{--class=" isRequired"--}%
                            %{--label="${message(code: 'recallToService.EnumServiceActionReason.label', default: 'serviceActionReason')}"/>--}%


                    <el:autocomplete optionKey="id"
                                     optionValue="name"
                                     size="6"
                                     class=" isRequired"
                                     paramsGenerateFunction="reasonParams"
                                     controller="serviceActionReason"
                                     action="autocomplete"
                                     id="serviceActionReasonId"
                                     name="serviceActionReason.id"
                                     label="${message(code: 'recallToService.EnumServiceActionReason.label', default: 'serviceActionReason')}"
                                     values="${[[employmentServiceRequest?.serviceActionReason?.id, employmentServiceRequest?.serviceActionReason?.descriptionInfo?.localName]]}"/>



                </el:formGroup>
                <el:formButton functionName="select" withClose="true" isSubmit="true"/>
                <el:formButton functionName="cancel" withPreviousLink="true" goToPreviousLink="true"/>
            </el:validatableResetForm>
        </el:row>
    </lay:widgetBody>
</lay:widget>
<script type="text/javascript">
    function reasonParams() {
        return {
            "firm.id": "${ps.police.common.utils.v1.PCPSessionUtils.getValue("firmId")}",
            "isRelatedToEndOfService_string": "NO"
        };
    }
</script>
</body>
</html>


