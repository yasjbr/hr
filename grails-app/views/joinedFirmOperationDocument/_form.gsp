<script>
    $(function(){
        resetDocumentsTable();
    });
</script>
<g:if test="${joinedFirmOperationDocument?.operation}">
    <el:formGroup>
        <el:select valueMessagePrefix="EnumOperation" disabled="true"
                   from="${ps.gov.epsilon.hr.enums.v1.EnumOperation.values()}"
                   name="operation" size="8" class=" isRequired"
                   label="${message(code: 'joinedFirmOperationDocument.operation.label', default: 'operation')}"
                   value="${joinedFirmOperationDocument?.operation}"/>
        <el:hiddenField name="operation" type="enum" value="${joinedFirmOperationDocument?.operation}"/>
    </el:formGroup>
</g:if>
<g:else>
    <el:formGroup id="operationSelectElement">

    </el:formGroup>
</g:else>



<el:row/>
<div>
    <div class="col-md-12">
        <table width="100%">
            <tr>
                <td style="width: 80%;">
                    <h4 class=" smaller lighter blue">${message(code: 'joinedFirmOperationDocument.firmDocument.label')}</h4>
                </td>
                <td width="180px" align="left">
                    <button type="button" class="btn  btn-bigger  btn-sm  btn-primary  btn-round"
                            id="firmDocumentBtn"
                            onclick='openFirmDocumentModal()'>
                    <g:message code="joinedFirmOperationDocument.addFirmDocument.label"/>
                    </button>
                </td>
            </tr>
        </table>
        <hr style="margin-top: 0px;margin-bottom: 8px;"/>
    </div>

    <el:formGroup>
        <div class="col-md-12" id="tableDiv">
        <lay:table styleNumber="1" id="detailsTable">
            <lay:tableHead title="${message(code: 'joinedFirmOperationDocument.document.label')}"/>
            <lay:tableHead title="${message(code: 'joinedFirmOperationDocument.isMandatory.label')}"/>
            <lay:tableHead title="${message(code: 'default.action')}"/>
                <g:each in="${joinedFirmOperationDocument?.transientData?.firmDocumentOperation}" var="operation"
                        status="index">
                    <rowElement>
                    <tr id="row-${index + 1}" class='center document-row'>
                        <td class='center'>${operation?.firmDocument?.descriptionInfo?.localName}</td>
                        <td class='center'>${operation?.isMandatoryTranslated}</td>
                        <td class='center'>

                            <input type='hidden' name='firmDocument' id='firmDocument-${index + 1}'
                                   value='${operation?.firmDocument?.id}'/>

                            <input type='hidden' name='isMandatory' id='isMandatory-${index + 1}'
                                   value='${operation?.isMandatory}'/>
                            <span class='delete-action'>
                                <a style='cursor: pointer;'
                                   class='red icon-trash '
                                   onclick="deleteRow('${index+1}', '${operation?.firmDocument?.id}');"
                                   title='<g:message code='default.button.delete.label'/>'>
                                </a>
                            </span>
                        </td>
                    </tr>
                    </rowElement>
                </g:each>
        </lay:table>

        </div>
    </el:formGroup>

</div>

<el:modal preventCloseOutSide="true" name="firmDocumentModal" id="firmDocumentModal"
          width="50%" hideCancel="true" method="post"
          title="${g.message(code: "joinedFirmOperationDocument.firmDocument.label")}">

    <el:modalButton class="btn  btn-bigger  btn-sm  btn-primary  btn-round"
                    icon="ace-icon fa fa-floppy-o"
                    onClick="addFirmDocument()"
                    id="firmDocumentAddBtn"
                    message="${g.message(code: "joinedFirmOperationDocument.addFirmDocument.label")}"/>

    <el:modalButton calss="btn  btn-bigger  btn-sm  btn-light  btn-round"
                    id="firmDocumentCancelBtn"
                    icon="ace-icon fa fa-times"
                    onClick="closeFirmDocumentModal()"
                    message="${g.message(code: "jobRequisition.previousWork.modal.close.label")}"/>

    <msg:modal/>

    <el:formGroup>
        <el:autocomplete optionKey="id" id="firmDocumentId" optionValue="name" size="8" class=" isRequired"
                         controller="firmDocument" action="autocomplete" name="firmDocumentId"
                         label="${message(code: 'joinedFirmOperationDocument.document.label', default: 'firmDocument')}"/>
    </el:formGroup>
    <el:formGroup>
        <el:checkboxField name="isMandatory" size="8" class=" "
                          label="${message(code: 'joinedFirmOperationDocument.isMandatory.label', default: 'isMandatory')}"/>
    </el:formGroup>
</el:modal>