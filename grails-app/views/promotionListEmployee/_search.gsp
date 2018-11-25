<el:formGroup>
    <el:textField name="request.id" size="6" class=" "
                     label="${message(code: 'updateMilitaryRankRequest.id.label', default: 'id')}"
                     value=""/>
    <el:autocomplete optionKey="id"
                     optionValue="name"
                     size="6" class=""
                     controller="employee"
                     action="autocomplete"
                     name="employee.id"
                     label="${message(code: 'employeePromotion.employee.label', default: 'employee')}"/>
</el:formGroup>

<el:formGroup>
    <el:autocomplete
            optionKey="id"
            optionValue="name"
            size="6"
            class=""
            controller="militaryRank"
            action="autocomplete"
            name="militaryRankId"
            label="${message(code: 'militaryRank.label', default: 'militaryRank')}"/>
    <el:range type="date" size="6" name="employmentDate" class=""
              label="${message(code:'promotionListEmployee.employee.employmentDate.label', default: 'employmentDate')}"/>
</el:formGroup>

<el:formGroup>
    <el:textField name="financialNumber" size="6" class=""
                  label="${message(code: 'employee.financialNumber.label', default: 'financialNumber')}"/>
    <el:autocomplete
            optionKey="id"
            optionValue="name"
            size="6"
            class=" "
            controller="department"
            action="autocomplete"
            name="departmentIdList"
            label="${message(code: 'promotionListEmployee.employee.currentEmploymentRecord.department.label', default: 'department')}"/>
</el:formGroup>

<el:formGroup>
    <el:select valueMessagePrefix="EnumPromotionReason"
               from="${ps.gov.epsilon.hr.enums.promotion.v1.EnumPromotionReason.values()}" name="promotionReason"
               size="6" class=""
               label="${message(code: 'promotionListEmployee.promotionReason.label', default: 'promotionReason')}"/>
    <el:autocomplete
            optionKey="id"
            optionValue="name"
            size="6"
            class=" "
            controller="militaryRank"
            action="autocomplete"
            name="militaryRank.id"
            label="${message(code: 'promotionListEmployee.militaryRank.label', default: 'militaryRank')}"/>
</el:formGroup>

<el:formGroup>
    <el:autocomplete
            optionKey="id"
            optionValue="name"
            size="6"
            class=" "
            controller="militaryRankType"
            action="autocomplete"
            name="militaryRankType.id"
            label="${message(code: 'militaryRankType.label', default: 'militaryRank')}"/>
    <el:autocomplete
            optionKey="id"
            optionValue="name"
            size="6"
            class=" "
            controller="militaryRankClassification"
            action="autocomplete"
            name="militaryRankClassification.id"
            label="${message(code: 'militaryRankClassification.label', default: 'militaryRankClassification')}"/>
</el:formGroup>

<el:formGroup>
    <el:select valueMessagePrefix="EnumListRecordStatus"
               from="${ps.gov.epsilon.hr.enums.v1.EnumListRecordStatus.values()}" name="recordStatus" size="6" class=""
               label="${message(code: 'promotionListEmployee.recordStatus.label', default: 'recordStatus')}"/>
</el:formGroup>
