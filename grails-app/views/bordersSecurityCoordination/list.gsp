<!DOCTYPE html>
<html>
<head>
    <meta name="layout" content="mainGUI"/>
    <g:set var="entities"
           value="${message(code: 'bordersSecurityCoordination.entities', default: 'BordersSecurityCoordination List')}"/>
    <g:set var="entity"
           value="${message(code: 'bordersSecurityCoordination.entity', default: 'BordersSecurityCoordination')}"/>
    <g:set var="title"
           value="${message(code: 'default.list.label', args: [entities], default: 'BordersSecurityCoordination List')}"/>
    <title>${title}</title>
</head>

<body>
<msg:page/>
<lay:collapseWidget id="bordersSecurityCoordinationCollapseWidget" icon="icon-search"
                    title="${message(code: 'default.search.label', args: [entities])}"
                    size="12" collapsed="true" data-toggle="collapse">
    <lay:widgetToolBar>
        <btn:buttonGroup>
            <btn:createButton
                    onClick="window.location.href='${createLink(controller: 'bordersSecurityCoordination', action: 'create')}'"/>
        </btn:buttonGroup>
    </lay:widgetToolBar>
    <lay:widgetBody>
        <el:form action="#" name="bordersSecurityCoordinationSearchForm">
            <g:render template="/bordersSecurityCoordination/search" model="[:]"/>
            <el:formButton functionName="search" onClick="_dataTables['bordersSecurityCoordinationTable'].draw()"/>
            <el:formButton functionName="clear"
                           onClick="gui.formValidatable.resetForm('bordersSecurityCoordinationSearchForm');_dataTables['bordersSecurityCoordinationTable'].draw();"/>
        </el:form>
    </lay:widgetBody>
</lay:collapseWidget>

<g:render template="/bordersSecurityCoordination/dataTable" model="[title:title]"/>
<g:render template="/attachment/attachmentCreateModal"/>
<g:render template="/attachment/attachmentListModal"/>
<g:render template="/attachment/attachmentPreviewModal"/>
<g:render template="/attachment/attachmentViewModal"/>
<g:render template="/attachment/attachmentEditModal"/>

<script>
    function checkRequestStatus(row) {
        if (row.requestStatus == "جديد") {
            return true;
        }
        return false;

    }

    function openAttachmentModal(row) {
        $("#attachmentListModal").attr('parentId', row);
        $("#attachmentListModal").attr('referenceObject', "${referenceObject}");
        $("#attachmentListModal").attr('parentType', 'normal');
        $("#attachmentListModal").attr('operationType', "${operationType}");
        $("#attachmentListModal").attr('attachmentTypeList', "${(attachmentTypeList?.encodeAsJSON())}");
        $("#attachmentListModal").attr('callBackFunction', '');
        $("#attachmentListModal").modal('show');
    }
</script>
</body>
</html>