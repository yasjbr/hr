<!DOCTYPE html>

<html>
<head>
    <meta name="layout" content="mainGUI" />

    <g:if test="${updateMilitaryRankRequest?.requestType == ps.gov.epsilon.hr.enums.v1.EnumRequestType.UPDATE_MILITARY_RANK_TYPE}">
        <g:set var="entity" value="${message(code: 'updateMilitaryRankTypeRequest.entity', default: 'updateMilitaryRankTypeRequest List')}" />
    </g:if><g:else>
        <g:set var="entity" value="${message(code: 'updateMilitaryRankClassification.entity', default: 'updateMilitaryRankClassification List')}" />
    </g:else>
    <g:set var="title" value="${message(code: 'default.create.label',args:[entity], default: 'UpdateMilitaryRankRequest List')}" />
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
            <el:validatableResetForm name="updateMilitaryRankRequestForm" callLoadingFunction="performPostActionWithEncodedId" controller="updateMilitaryRankRequest" action="save">
                <g:render template="/updateMilitaryRankRequest/form" model="[updateMilitaryRankRequest:updateMilitaryRankRequest]"/>
                <el:formButton functionName="save" withClose="true" isSubmit="true"/>
                <el:formButton functionName="cancel" withPreviousLink="true" goToPreviousLink="true"/>
            </el:validatableResetForm>
        </el:row>
    </lay:widgetBody>
</lay:widget>
<script>
    function oldRankTypeParams() {
        return {'idToExclude':$("#newRankTypeId").val()}
    }
    function newRankTypeParams() {
        return {'idToExclude':$("#oldRankTypeId").val()}
    }
    function oldRankClassificationParams() {
        return {'idToExclude':$("#newRankClassificationId").val()}
    }
    function newRankClassificationParams() {
        return {'idToExclude':$("#oldRankClassificationId").val()}
    }
</script>
</body>
</html>
