<%@ page import="ps.gov.epsilon.hr.enums.v1.EnumListRecordStatus" %>

<script type="text/javascript">
    function noteCallBackFun(json){
        if (json.success) {
            $('#listRecordTableInAocList').find('a.modal-ajax_${aocListRecordNoteInstance?.listRecord?.id}.showNoteList').click();
        }
    }
    function statusCallBackFun(json){
        if (json.success) {
            $('#application-modal-main-content').modal("hide");
            _dataTables['listRecordTableInAocList'].draw();
        }
    }
</script>

<el:validatableModalForm title="${noteFormTitle}" width="70%" name="${formName}"
                         controller="${saveController}" hideCancel="true" hideClose="true"
                         action="${saveAction}" callBackFunction="${callBackFunction}">
    <msg:modal/>

    <g:if test="${viewChangeStatus}">
        <el:formGroup>
            <el:select valueMessagePrefix="EnumListRecordStatus" name="recordStatus" size="8" class="isRequired"
                       from="${[EnumListRecordStatus.APPROVED, EnumListRecordStatus.REJECTED]}"
                       label="${message(code: 'aocAllowanceListRecord.recordStatus.label', default: 'recordStatus')}"
                       value="" onChange="showHideAcceptForm()"/>
        </el:formGroup>

        <div id="acceptForm" style="display:none">
            %{--accept form will be rendered here in case of approve choice is selected--}%
            <g:if test="${parentFolder}">
                <g:render template="/${parentFolder}/recordAcceptForm"
                          model="[listEmployee:aocListRecordNoteInstance?.listRecord?.hrListEmployee, colSize:8]" />
            </g:if>
        </div>

    </g:if>

    <el:formGroup>
        <el:hiddenField name="listRecord.id" value="${aocListRecordNoteInstance?.listRecord?.id}"/>
        %{--<el:textField name="orderNo" size="8" class=""--}%
                      %{--label="${message(code: 'promotionListEmployeeNote.orderNo.label', default: 'orderNo')}"--}%
                      %{--value="${aocListRecordNoteInstance?.orderNo}"/>--}%
    </el:formGroup>
    <el:formGroup>
        <el:dateField name="noteDate" size="8" class=" isRequired"
                      label="${message(code: 'promotionListEmployeeNote.noteDate.label', default: 'noteDate')}"
                      value="${aocListRecordNoteInstance?.noteDate}"/>
    </el:formGroup>
    <el:formGroup>
        <el:textArea name="note" size="8" class="isRequired"
                     label="${message(code: 'promotionListEmployeeNote.note.label', default: 'note')}"
                     value="${aocListRecordNoteInstance?.note}"/>
    </el:formGroup>

%{--<el:formGroup>--}%
%{--<el:checkboxField name="rejectRequest" size="8" label="${message(code: 'dispatchRequest.reject.label', default: 'note')}" />--}%
%{--</el:formGroup>--}%

    <el:formButton isSubmit="true" functionName="save"/>

    <g:if test="${viewChangeStatus}">
        <el:formButton onClick="closeStatusForm()" functionName="cancel"/>
    </g:if>
    <g:else>
        <el:formButton onClick="closeNoteForm()" functionName="cancel"/>
    </g:else>
</el:validatableModalForm>

<script>

    $('#application-modal-main-content').on("hidden.bs.modal", function () {
        var isCreate = $(this).find('#listRecordNoteForm').length;
        if(isCreate > 0){
            $('#listRecordTableInAocList').find('a.modal-ajax_${aocListRecordNoteInstance?.listRecord?.id}.showNoteList').click();
        }
    });

    function closeNoteForm() {
        $('#listRecordTableInAocList').find('a.modal-ajax_${aocListRecordNoteInstance?.listRecord?.id}.showNoteList').click();

    }

    function closeStatusForm() {
        $('#application-modal-main-content').modal("hide");
    }

    /**
     * gets an html page in ajax
     * @param params
     */
    function showHideAcceptForm(){
        var status= $("#recordStatus").val();
        var formDiv= $("#acceptForm");
        if(status == "${ps.gov.epsilon.hr.enums.v1.EnumListRecordStatus.APPROVED.name()}"){
            formDiv.show(500);
        }else{
            formDiv.hide(100);
        }
    }

</script>