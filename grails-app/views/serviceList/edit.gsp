<!DOCTYPE html>
<html>
<head>
    <meta name="layout" content="mainGUI" />

    <g:if test="${serviceList.serviceListType == ps.gov.epsilon.hr.enums.employmentService.v1.EnumServiceListType.RETURN_TO_SERVICE}" >
        <g:set var="entity" value="${message(code: 'recallToServiceList.entity', default: 'recallToServiceList List')}" />
        <g:set var="title" value="${message(code: 'default.edit.label',args:[entity], default: 'recallToServiceList List')}" />
    </g:if>
    <g:else>
        <g:set var="entity" value="${message(code: 'endOfServiceList.entity', default: 'endOfServiceList List')}" />
        <g:set var="title" value="${message(code: 'default.edit.label',args:[entity], default: 'endOfServiceList List')}" />
    </g:else>

    <title>${title}</title>
</head>
<body>
<lay:widget title="${title}">
    <lay:widgetToolBar>
        <btn:buttonGroup>
            <g:if test="${serviceList.serviceListType == ps.gov.epsilon.hr.enums.employmentService.v1.EnumServiceListType.RETURN_TO_SERVICE}" >
                <btn:listButton onClick="window.location.href='${createLink(controller:'serviceList',action:'listReturnToServiceList')}'"/>
            </g:if>
            <g:else>
                <btn:listButton onClick="window.location.href='${createLink(controller:'serviceList',action:'listEndOfServiceList')}'"/>
            </g:else>
        </btn:buttonGroup>
    </lay:widgetToolBar>
    <lay:widgetBody>
        <el:row>
            <msg:page />
            <el:validatableForm name="serviceListForm" controller="serviceList" action="update">
                <g:render template="/serviceList/form" model="[serviceList:serviceList, enumServiceListTypeList:enumServiceListTypeList]"/>
                <el:hiddenField name="encodedId" value="${serviceList?.encodedId}" />
                <el:formButton isSubmit="true" functionName="save" withPreviousLink="true"/>
                <el:formButton functionName="cancel" withPreviousLink="true" goToPreviousLink="true"/>
            </el:validatableForm>
        </el:row>
    </lay:widgetBody>
</lay:widget>
</body>
</html>