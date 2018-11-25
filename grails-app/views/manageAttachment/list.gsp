<%@ page import="grails.util.Holders" %>
<!DOCTYPE html>
<html>
<head>
    <meta name="layout" content="mainGUI"/>
    <g:set var="entities" value="${message(code: 'attachment.entities', default: 'Attachment List')}"/>
    <g:set var="entity" value="${message(code: 'attachment.entity', default: 'Attachment')}"/>
    <g:set var="title" value="${message(code: 'default.list.label', args: [entities], default: 'Attachment List')}"/>
    <title>${title}</title>
</head>

<body>
<msg:page/>


<lay:collapseWidget id="attachmentCollapseWidget" icon="icon-search"
                    title="${message(code: 'attachmentListModal.search')}"
                    size="12" collapsed="true" data-toggle="collapse">
    <lay:widgetBody>
        <el:form action="#" name="attachmentSearchForm">
            <g:render template="/attachment/attachmentSearchForm" model="[:]"/>
            <el:hiddenField name="sourceApplication" value="${grails.util.Holders.grailsApplication.config.grails.applicationName}"/>
            <el:formButton functionName="search" onClick="_dataTables['attachmentTable2'].draw()"/>
            <el:formButton functionName="clear" onClick="gui.formValidatable.resetForm('attachmentSearchForm');_dataTables['attachmentTable2'].draw()"/>
        </el:form>
    </lay:widgetBody>
</lay:collapseWidget>


<g:render template="/attachment/attachmentTab" model="[:]"/>

</body>
</html>