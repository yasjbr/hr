<!DOCTYPE html>
<html>
<head>
    <meta name="layout" content="mainGUI" />
    <g:if test="${updateMilitaryRankRequest?.requestType == ps.gov.epsilon.hr.enums.v1.EnumRequestType.UPDATE_MILITARY_RANK_TYPE}">
        <g:set var="entity" value="${message(code: 'updateMilitaryRankTypeRequest.entity', default: 'updateMilitaryRankTypeRequest List')}" />
    </g:if><g:else>
        <g:set var="entity" value="${message(code: 'updateMilitaryRankClassification.entity', default: 'updateMilitaryRankClassification List')}" />
    </g:else>
    <g:set var="title" value="${message(code: 'default.edit.label',args:[entity], default: 'UpdateMilitaryRankRequest List')}" />
    <title>${title}</title>

</head>
<body>
<lay:widget title="${title}">
    <lay:widgetToolBar>
        <btn:buttonGroup>
            <btn:listButton onClick="window.location.href='${createLink(controller:'updateMilitaryRankRequest',action:'list')}'"/>
        </btn:buttonGroup>
    </lay:widgetToolBar>
    <lay:widgetBody>
        <el:row>
            <msg:page />
            <el:validatableForm name="updateMilitaryRankRequestForm" controller="updateMilitaryRankRequest" action="update">
                <g:render template="/updateMilitaryRankRequest/form" model="[updateMilitaryRankRequest:updateMilitaryRankRequest]"/>
                <el:hiddenField name="id" value="${updateMilitaryRankRequest?.id}" />
                <el:formButton isSubmit="true" functionName="save" withPreviousLink="true" />
                <el:formButton functionName="cancel" goToPreviousLink="true"/>
            </el:validatableForm>
        </el:row>
    </lay:widgetBody>
</lay:widget>
</body>
</html>