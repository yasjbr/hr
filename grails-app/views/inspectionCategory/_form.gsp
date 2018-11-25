<g:render template="/DescriptionInfo/wrapper" model="[bean: inspectionCategory?.descriptionInfo]"/>
<el:formGroup>
    <el:integerField name="orderId" size="8" class=" isRequired isNumber"
                     label="${message(code: 'inspection.orderId.label', default: 'orderId')}"
                     value="${inspectionCategory?.orderId}"/>
</el:formGroup>
<el:formGroup>
    <el:checkboxField name="isRequiredByFirmPolicy" size="8" class=""
                      label="${message(code: 'inspectionCategory.isRequiredByFirmPolicy.label', default: 'isRequiredByFirmPolicy')}"
                      value="${inspectionCategory?.isRequiredByFirmPolicy}"
                      isChecked="${inspectionCategory?.isRequiredByFirmPolicy}"/>
</el:formGroup>
<el:formGroup>
    <el:checkboxField name="hasMark" size="8" class=""
                      label="${message(code: 'inspectionCategory.hasMark.label', default: 'hasMark')}"
                      value="${inspectionCategory?.hasMark}"
                      isChecked="${inspectionCategory?.hasMark}"/>
</el:formGroup>
<el:formGroup>
    <el:checkboxField name="hasResultRate" size="8" class=""
                      label="${message(code: 'inspectionCategory.hasResultRate.label', default: 'hasResultRate')}"
                      value="${inspectionCategory?.hasResultRate}" isChecked="${inspectionCategory?.hasResultRate}"/>
</el:formGroup>
<el:formGroup>
    <el:textArea name="description" size="8" class=""
                 label="${message(code: 'inspectionCategory.description.label', default: 'description')}"
                 value="${inspectionCategory?.description}"/>
</el:formGroup>

<el:formGroup>
    <el:dualListBox size="8" optionKey="id" id="listBox" from="${committeeRoleList}"
                    values="${inspectionCategory?.committeeRoles?.committeeRole}"
                    label="${message(code: 'inspectionCategory.committeeRole.label', default: 'committee role')}"
                    name="committeeRoles.id"/>
</el:formGroup>
<el:formGroup>
    <el:textField name="universalCode" size="8" class=""
                  label="${message(code: 'inspectionCategory.universalCode.label', default: 'universalCode')}"
                  value="${inspectionCategory?.universalCode}"/>
</el:formGroup>
<el:formGroup>
    <el:textArea name="note" size="8" class=""
                 label="${message(code: 'inspectionCategory.note.label', default: 'note')}"
                 value="${inspectionCategory?.note}"/>
</el:formGroup>