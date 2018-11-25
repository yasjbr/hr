

<g:render template="/employee/wrapper" model="[id:(employeeCallBackId?:'employeeId'),
                                               name:'employee.id',
                                               isHiddenInfo:params.isHiddenPersonInfo,
                                               bean:profileNote?.employee,
                                               isDisabled:isEmployeeDisabled]" />
<el:formGroup>
    <el:textField name="orderNo" size="8"  class="" label="${message(code:'profileNote.orderNo.label',default:'orderNo')}" value="${profileNote?.orderNo}"/>
</el:formGroup>

<el:formGroup>
    <el:dateField name="noteDate"  size="8" class=" isRequired" label="${message(code:'profileNote.noteDate.label',default:'noteDate')}" value="${profileNote?.noteDate}" />
</el:formGroup>


<el:formGroup>
    <el:textArea name="note" size="8"  class=" isRequired" label="${message(code:'profileNote.note.label',default:'note')}" value="${profileNote?.note}"/>
</el:formGroup>

