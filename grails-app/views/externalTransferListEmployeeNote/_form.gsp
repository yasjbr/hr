
<el:formGroup>
    <el:autocomplete optionKey="id" optionValue="name" size="8" class=" isRequired" controller="externalTransferListEmployee" action="autocomplete" name="externalTransferListEmployee.id" label="${message(code:'externalTransferListEmployeeNote.externalTransferListEmployee.label',default:'externalTransferListEmployee')}" values="${[[externalTransferListEmployeeNote?.externalTransferListEmployee?.id,externalTransferListEmployeeNote?.externalTransferListEmployee?.descriptionInfo?.localName]]}" />
</el:formGroup>
<el:formGroup>
    <el:textField name="note" size="8"  class="" label="${message(code:'externalTransferListEmployeeNote.note.label',default:'note')}" value="${externalTransferListEmployeeNote?.note}"/>
</el:formGroup>
<el:formGroup>
    <el:dateField name="noteDate"  size="8" class=" isRequired" label="${message(code:'externalTransferListEmployeeNote.noteDate.label',default:'noteDate')}" value="${externalTransferListEmployeeNote?.noteDate}" />
</el:formGroup>
<el:formGroup>
    <el:textField name="orderNo" size="8"  class="" label="${message(code:'externalTransferListEmployeeNote.orderNo.label',default:'orderNo')}" value="${externalTransferListEmployeeNote?.orderNo}"/>
</el:formGroup>