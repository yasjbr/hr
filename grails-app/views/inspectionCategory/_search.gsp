<el:formGroup>
    <el:textField name="id" size="8" class=" "
                  label="${message(code: 'id.label', default: 'id')}"/>
</el:formGroup>
<el:formGroup>
    <el:textField class=""
                  name="descriptionInfo.localName" size="8"
                  label="${message(code:'descriptionInfo.localName.label',default:'localName')}"
                   />
</el:formGroup>
<el:formGroup>
    <el:select
            label="${message(code: 'inspectionCategory.isRequiredByFirmPolicy.label', default: 'isRequiredByFirmPolicy')}"
            name="isRequiredByFirmPolicy"
            size="8"
            class="" from="['','true','false']" valueMessagePrefix="select"
            placeholder="${message(code: 'default.select.label', default: 'please select')}"/>

</el:formGroup>
<el:formGroup>
    <el:select label="${message(code: 'inspectionCategory.hasResultRate.label', default: 'hasResultRate')}"
               name="hasResultRate"
               size="8"
               class="" from="['','true','false']" valueMessagePrefix="select"
               placeholder="${message(code: 'default.select.label', default: 'please select')}"/>

</el:formGroup>
<el:formGroup>

    <el:select label="${message(code: 'inspectionCategory.hasMark.label', default: 'hasMark')}"
               name="hasMark"
               size="8"
               class="" from="['','true','false']" valueMessagePrefix="select"
               placeholder="${message(code: 'default.select.label', default: 'please select')}"/>

</el:formGroup>
<el:formGroup>
    <el:integerField name="orderId" size="8" class="isNumber"
                     label="${message(code: 'inspection.orderId.label', default: 'orderId')}"/>
</el:formGroup>

