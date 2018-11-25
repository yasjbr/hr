<!DOCTYPE html>

<html>
<head>
    <meta name="layout" content="mainGUI" />
    <g:set var="entity" value="${message(code: 'trainingCourse.entity', default: 'TrainingCourse List')}" />
    <g:set var="title" value="${message(code: 'default.create.label',args:[entity], default: 'TrainingCourse List')}" />
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
            <el:validatableResetForm name="trainingCourseForm" callLoadingFunction="performPostActionWithEncodedId" controller="trainingCourse" action="save">
                <g:render template="/trainingCourse/form" model="[trainingCourse:trainingCourse]"/>
                <el:formButton functionName="save" withClose="true" isSubmit="true"/>

            </el:validatableResetForm>
        </el:row>
    </lay:widgetBody>
</lay:widget>
</body>
</html>
