<!DOCTYPE html>
<html>
<head>
    <meta name="layout" content="mainGUI"/>
    <g:if test="${employmentServiceRequest.requestType == ps.gov.epsilon.hr.enums.v1.EnumRequestType.RETURN_TO_SERVICE}">
        <g:set var="entity" value="${message(code: 'recallToService.entity', default: 'recallToService List')}"/>
        <g:set var="title"
               value="${message(code: 'default.edit.label', args: [entity], default: 'recallToService List')}"/>
    </g:if>
    <g:else>
        <g:set var="entity" value="${message(code: 'endOfService.entity', default: 'endOfService List')}"/>
        <g:set var="title"
               value="${message(code: 'default.edit.label', args: [entity], default: 'endOfService List')}"/>
    </g:else>
    <title>${title}</title>
</head>
<body>
<lay:widget title="${title}">
    <lay:widgetToolBar>
        <btn:buttonGroup>
            <g:if test="${employmentServiceRequest.requestType == ps.gov.epsilon.hr.enums.v1.EnumRequestType.RETURN_TO_SERVICE}">
                <btn:listButton
                        onClick="window.location.href='${createLink(controller: 'employmentServiceRequest', action: 'listReturnToService')}'"/>
            </g:if>
            <g:else>
                <btn:listButton
                        onClick="window.location.href='${createLink(controller: 'employmentServiceRequest', action: 'listEndOfService')}'"/>
            </g:else>
        </btn:buttonGroup>
    </lay:widgetToolBar>
    <lay:widgetBody>
        <el:row>
            <msg:page/>
            <el:validatableForm name="employmentServiceRequestForm" controller="employmentServiceRequest"
                                action="update">
                <g:if test="${employmentServiceRequest.requestType == ps.gov.epsilon.hr.enums.v1.EnumRequestType.RETURN_TO_SERVICE}">
                    <g:render template="/employmentServiceRequest/formReturnToService"
                              model="[employmentServiceRequest: employmentServiceRequest]"/>
                </g:if>
                <g:else>
                    <g:render template="/employmentServiceRequest/formEndOfService"
                              model="[employmentServiceRequest: employmentServiceRequest]"/>
                </g:else>
                <el:hiddenField name="id" value="${employmentServiceRequest?.id}"/>
                <el:formButton isSubmit="true" functionName="save" withPreviousLink="true"/>
                <el:formButton functionName="cancel" withPreviousLink="true" goToPreviousLink="true"/>
            </el:validatableForm>
        </el:row>
    </lay:widgetBody>
</lay:widget>
</body>
</html>