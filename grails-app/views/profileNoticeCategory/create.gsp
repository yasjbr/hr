<!DOCTYPE html>

<html>
<head>
    <meta name="layout" content="mainGUI" />
    <g:set var="entity" value="${message(code: 'profileNoticeCategory.entity', default: 'ProfileNoticeCategory List')}" />
    <g:set var="title" value="${message(code: 'default.create.label',args:[entity], default: 'ProfileNoticeCategory List')}" />
    <title>${title}</title>
</head>
<body>
<lay:widget title="${title}">
    <lay:widgetToolBar>
        <btn:buttonGroup>
            <btn:listButton onClick="window.location.href='${createLink(controller:'profileNoticeCategory',action:'list')}'"/>
        </btn:buttonGroup>
    </lay:widgetToolBar>
    <lay:widgetBody>
        <el:row>
            <msg:page />
            <el:validatableResetForm name="profileNoticeCategoryForm" callLoadingFunction="performPostActionWithEncodedId" controller="profileNoticeCategory" action="save">
                <g:render template="/profileNoticeCategory/form" model="[profileNoticeCategory:profileNoticeCategory]"/>
                <el:formButton functionName="save" withClose="true" isSubmit="true"/>
                <el:formButton functionName="cancel" goToPreviousLink="true"/>
            </el:validatableResetForm>
        </el:row>
    </lay:widgetBody>
</lay:widget>
</body>
</html>
