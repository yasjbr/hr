<!DOCTYPE html>
<html>
<head>
    <meta name="layout" content="mainGUI" />
    <g:set var="entities" value="${message(code: 'trainingCourse.entities', default: 'TrainingCourse List')}" />
    <g:set var="entity" value="${message(code: 'trainingCourse.entity', default: 'TrainingCourse')}" />
    <g:set var="title" value="${message(code: 'default.list.label',args:[entities], default: 'TrainingCourse List')}" />
    <title>${title}</title>
</head>
<body>
<msg:page />
<lay:collapseWidget id="trainingCourseCollapseWidget" icon="icon-search"
                    title="${message(code:'default.search.label',args:[entities])}"
                    size="12" collapsed="true" data-toggle="collapse" >
    <lay:widgetToolBar>
        <btn:buttonGroup>
            <btn:createButton onClick="window.location.href='${createLink(controller:'trainingCourse',action:'create')}'"/>
        </btn:buttonGroup>
    </lay:widgetToolBar>
    <lay:widgetBody>
        <el:form action="#" name="trainingCourseSearchForm">
            <g:render template="/trainingCourse/search" model="[:]"/>
            <el:formButton functionName="search" onClick="_dataTables['trainingCourseTable'].draw()"/>
            <el:formButton functionName="clear" onClick="gui.formValidatable.resetForm('trainingCourseSearchForm');_dataTables['trainingCourseTable'].draw();"/>
        </el:form>
    </lay:widgetBody>
</lay:collapseWidget>
<el:dataTable id="trainingCourseTable" searchFormName="trainingCourseSearchForm"
              title="${title}"
              dataTableTitle="${title}"
              hasCheckbox="true" widthClass="col-sm-12" controller="trainingCourse" spaceBefore="true" hasRow="true" action="filter" serviceName="trainingCourse">
    <el:dataTableAction controller="trainingCourse" action="show" actionParams="encodedId" class="green icon-eye" message="${message(code:'default.show.label',args:[entity],default:'show trainingCourse')}" />
    <el:dataTableAction controller="trainingCourse" action="edit" actionParams="encodedId" class="blue icon-pencil" message="${message(code:'default.edit.label',args:[entity],default:'edit trainingCourse')}" />
    <el:dataTableAction controller="trainingCourse" action="delete" actionParams="encodedId" class="red icon-trash" type="confirm-delete" message="${message(code:'default.delete.label',args:[entity],default:'delete trainingCourse')}" />
</el:dataTable>
</body>
</html>