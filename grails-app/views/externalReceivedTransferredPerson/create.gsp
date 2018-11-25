<!DOCTYPE html>

<html>
<head>
    <meta name="layout" content="mainGUI" />
    <g:set var="entity" value="${message(code: 'externalReceivedTransferredPerson.entity', default: 'externalReceivedTransferredPerson')}" />
    <g:set var="title" value="${message(code: 'default.create.label',args:[entity], default: 'externalReceivedTransferredPerson')}" />
    <title>${title}</title>

    <script>
        function successCallBack(json) {
            if(json.success){
                window.location.href = "${createLink(controller: 'externalReceivedTransferredPerson',action: 'createNewExternalReceived')}?personId="+json.personId;
            }
        }
        function confirmAddPerson() {
            gui.confirm.confirmFunc("${message(code: 'person.addNew.confirm.title')}", "${message(code: 'person.addNew.confirm.message')}", function () {
                window.location.href='${createLink(controller: 'externalReceivedTransferredPerson', action: 'createNewPerson')}';
            });
        }
    </script>
</head>
<body>

<lay:widget title="${title}">
    <lay:widgetToolBar>
        <btn:buttonGroup>
            <btn:listButton onClick="window.location.href='${createLink(controller: 'externalReceivedTransferredPerson', action: 'list')}'"/>
        </btn:buttonGroup>
    </lay:widgetToolBar>
    <lay:widgetBody>
        <el:row>
            <msg:page/>
            <el:validatableResetForm callBackFunction="successCallBack" name="externalReceivedTransferredPersonForm"
                                     controller="externalReceivedTransferredPerson" action="getPerson">
                <el:formGroup>
                    <el:autocomplete
                            optionKey="id"
                            optionValue="name"
                            size="8"
                            class=" isRequired"
                            controller="person"
                            action="autocomplete"
                            name="personId"
                            label="${message(code: 'externalReceivedTransferredPerson.searchPerson.label', default: 'externalReceivedTransferredPerson')}"/>
                </el:formGroup>
                <el:formButton functionName="select" isSubmit="true" />
                <el:formButton functionName="addButton" onclick="confirmAddPerson()" />
                <el:formButton functionName="cancel" goToPreviousLink="true" />
            </el:validatableResetForm>
        </el:row>
    </lay:widgetBody>
</lay:widget>

</body>
</html>