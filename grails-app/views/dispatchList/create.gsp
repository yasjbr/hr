<!DOCTYPE html>

<html>
<head>
    <meta name="layout" content="mainGUI" />
    <g:set var="entity" value="${message(code: 'dispatchList.entity', default: 'DispatchList List')}" />
    <g:set var="title" value="${message(code: 'default.create.label',args:[entity], default: 'DispatchList List')}" />
    <title>${title}</title>
</head>
<body>
<lay:widget title="${title}">
    <lay:widgetToolBar>
        <btn:buttonGroup>
            <btn:listButton onClick="window.location.href='${createLink(controller:'dispatchList',action:'list')}'"/>
        </btn:buttonGroup>
    </lay:widgetToolBar>
    <lay:widgetBody>
        <el:row>
            <msg:page />
            <el:validatableResetForm name="dispatchListForm" callLoadingFunction="performPostActionWithEncodedId" controller="dispatchList" action="save">
                <g:render template="/dispatchList/form" model="[dispatchList:dispatchList]"/>
                <el:formButton functionName="save" withClose="true" isSubmit="true"/>
                <el:formButton functionName="cancel" withPreviousLink="true" goToPreviousLink="true"/>
            </el:validatableResetForm>
        </el:row>
    </lay:widgetBody>
</lay:widget>
</body>
</html>
