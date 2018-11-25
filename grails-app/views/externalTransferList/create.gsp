<!DOCTYPE html>

<html>
<head>
    <meta name="layout" content="mainGUI"/>
    <g:set var="entity" value="${message(code: 'externalTransferList.entity', default: 'ExternalTransferList List')}"/>
    <g:set var="title" value="${message(code: 'default.create.label', args: [entity], default: 'ExternalTransferList List')}"/>
    <title>${title}</title>
</head>

<body>
<lay:widget title="${title}">
    <lay:widgetToolBar>
        <btn:buttonGroup>
            <btn:listButton
                    onClick="window.location.href='${createLink(controller: 'externalTransferList', action: 'list')}'"/>
        </btn:buttonGroup>
    </lay:widgetToolBar>
    <lay:widgetBody>
        <el:row>
            <msg:page/>
            <el:validatableResetForm name="externalTransferListForm" callLoadingFunction="performPostActionWithEncodedId"
                                     controller="externalTransferList" action="save">
                <g:render template="/externalTransferList/form" model="[externalTransferList: externalTransferList]"/>
                <el:formButton functionName="save" withClose="true" isSubmit="true"/>
                <el:formButton functionName="cancel" withPreviousLink="true" goToPreviousLink="true"/>
            </el:validatableResetForm>
        </el:row>
    </lay:widgetBody>
</lay:widget>
</body>
</html>
