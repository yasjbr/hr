<!DOCTYPE html>
<html>
<head>
    <meta name="layout" content="mainGUI" />
    <g:set var="title" value="${message(code: 'externalTransferRequest.addClearance.label', default: 'add clearance')}" />
    <title>${title}</title>
</head>
<body>
<lay:widget title="${title}">
    <lay:widgetToolBar>
        <btn:buttonGroup>
            <btn:listButton onClick="window.location.href='${createLink(controller:'externalTransferRequest',action:'list')}'"/>
        </btn:buttonGroup>
    </lay:widgetToolBar>
    <lay:widgetBody>
        <el:row>
            <msg:page />
            <el:validatableForm
                    callLoadingFunction="performPostActionWithEncodedId"
                    name="externalTransferRequestForm" controller="externalTransferRequest"
                    action="saveClearance">

                <el:hiddenField name="id" value="${externalTransferRequest?.id}" />

                <g:render template="requestInfoWrapper" />


                <lay:widget transparent="true" color="blue" icon="icon-info-4" title="${g.message(code: "externalTransferRequest.clearanceInfo.label")}">
                    <lay:widgetBody>

                        <el:formGroup>


                            <el:textField name="clearanceOrderNo" size="6"  class=" isRequired"
                                          label="${message(code:'externalTransferRequest.clearanceOrderNo.label',default:'clearanceOrderNo')}"
                                          value="${externalTransferRequest?.clearanceOrderNo}"/>


                            <el:dateField name="clearanceDate"  size="6" class=" isRequired" isMaxDate="true"
                                          label="${message(code:'externalTransferRequest.clearanceDate.label',default:'clearanceDate')}"
                                          value="${externalTransferRequest?.clearanceDate}" />



                        </el:formGroup>

                        <el:formGroup>

                            <el:textArea name="clearanceNote" size="6"
                                         class=" " label="${message(code:'externalTransferRequest.clearanceNote.label',default:'clearanceNote')}"
                                         value="${externalTransferRequest?.clearanceNote}"/>

                        </el:formGroup>


                    </lay:widgetBody>

                </lay:widget>




                <el:formButton functionName="saveAndClose" withClose="true" isSubmit="true" />
                <el:formButton functionName="cancel" goToPreviousLink="true" />

            </el:validatableForm>
        </el:row>
    </lay:widgetBody>
</lay:widget>
</body>
</html>