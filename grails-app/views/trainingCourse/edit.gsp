<!DOCTYPE html>
<html>
<head>
    <meta name="layout" content="mainGUI" />
    <g:set var="entity" value="${message(code: 'trainingCourse.entity', default: 'TrainingCourse List')}" />
    <g:set var="title" value="${message(code: 'default.edit.label',args:[entity], default: 'TrainingCourse List')}" />
    <title>${title}</title>

</head>
<body>
<lay:widget title="${title}">
    <lay:widgetToolBar>
        <btn:buttonGroup>
            <btn:listButton onClick="window.location.href='${createLink(controller:'trainingCourse',action:'list')}'"/>
        </btn:buttonGroup>
    </lay:widgetToolBar>
    <lay:widgetBody>
        <el:row>
            <msg:page />
            <el:validatableForm name="trainingCourseForm" controller="trainingCourse" action="update">
                <g:render template="/trainingCourse/form" model="[trainingCourse:trainingCourse]"/>
                <el:hiddenField name="id" value="${trainingCourse?.id}" />
                <el:formButton isSubmit="true" functionName="save"/>
            </el:validatableForm>
        </el:row>
    </lay:widgetBody>
</lay:widget>
</body>
</html>