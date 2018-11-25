<!DOCTYPE html>

<html>
<head>
    <meta name="layout" content="mainGUI"/>
    <g:set var="entity" value="${message(code: 'disciplinaryCategory.entity', default: 'DisciplinaryCategory List')}"/>
    <g:set var="title"
           value="${message(code: 'default.create.label', args: [entity], default: 'DisciplinaryCategory List')}"/>
    <title>${title}</title>
</head>

<body>
<lay:widget title="${title}">
    <lay:widgetToolBar>
        <btn:buttonGroup>
            <btn:listButton
                    onClick="window.location.href='${createLink(controller: 'disciplinaryCategory', action: 'list')}'"/>
        </btn:buttonGroup>
    </lay:widgetToolBar>
    <lay:widgetBody>
        <el:row>
            <msg:page/>
            <el:validatableResetForm name="disciplinaryCategoryForm" controller="disciplinaryCategory" action="save" callLoadingFunction="performPostActionWithEncodedId">
                <g:render template="/disciplinaryCategory/form" model="[disciplinaryCategory: disciplinaryCategory]"/>
                <el:formButton functionName="saveAndCreate" isSubmit="true"/>
                <el:formButton functionName="saveAndClose" withPreviousLink="true" isSubmit="true" />
                <el:formButton functionName="cancel" goToPreviousLink="true" />
            </el:validatableResetForm>
        </el:row>
    </lay:widgetBody>
</lay:widget>
</body>
</html>
