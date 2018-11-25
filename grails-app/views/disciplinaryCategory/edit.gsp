<!DOCTYPE html>
<html>
<head>
    <meta name="layout" content="mainGUI" />
    <g:set var="entity" value="${message(code: 'disciplinaryCategory.entity', default: 'DisciplinaryCategory List')}" />
    <g:set var="title" value="${message(code: 'default.edit.label',args:[entity], default: 'DisciplinaryCategory List')}" />
    <title>${title}</title>
</head>
<body>
<lay:widget title="${title}">
    <lay:widgetToolBar>
        <btn:buttonGroup>
            <btn:listButton onClick="window.location.href='${createLink(controller:'disciplinaryCategory',action:'list')}'"/>
        </btn:buttonGroup>
    </lay:widgetToolBar>
    <lay:widgetBody>
        <el:row>
            <msg:page />
            <el:validatableForm callLoadingFunction="performPostActionWithEncodedId" name="disciplinaryCategoryForm" controller="disciplinaryCategory" action="update">
                <g:render template="/disciplinaryCategory/form" model="[disciplinaryCategory:disciplinaryCategory]"/>
                <el:hiddenField name="id" value="${disciplinaryCategory?.id}" />

                <el:formButton functionName="save" withPreviousLink="true" isSubmit="true" />
                <el:formButton functionName="cancel" goToPreviousLink="true" />

            </el:validatableForm>
        </el:row>
    </lay:widgetBody>
</lay:widget>
</body>
</html>