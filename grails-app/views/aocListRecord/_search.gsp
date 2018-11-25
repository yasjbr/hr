
<el:formGroup>
    <el:dateField name="orderDate"  size="8" class="" label="${message(code:'aocListRecord.orderDate.label',default:'orderDate')}" />
</el:formGroup>
<el:formGroup>
    
    <el:textField name="orderNo" size="8"  class="" label="${message(code:'aocListRecord.orderNo.label',default:'orderNo')}" />
</el:formGroup>
<el:formGroup>
    
    <el:textField name="orderNotes" size="8"  class="" label="${message(code:'aocListRecord.orderNotes.label',default:'orderNotes')}" />
</el:formGroup>
<el:formGroup>
    <el:select valueMessagePrefix="EnumListRecordStatus" from="${ps.gov.epsilon.hr.enums.v1.EnumListRecordStatus.values()}" name="recordStatus" size="8"  class="" label="${message(code:'aocListRecord.recordStatus.label',default:'recordStatus')}" />
</el:formGroup>
