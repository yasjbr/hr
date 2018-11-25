<el:row />
<el:row />
<lay:showWidget size="12" title="${title}">
    <lay:showElement value="${employeeSalaryInfo?.transientData?.bankDTO?.descriptionInfo?.localName}" type="Long" label="${message(code:'employeeSalaryInfo.transientData.bankDTO.descriptionInfo.localName.label',default:'bankId')}" />
    <lay:showElement value="${employeeSalaryInfo?.transientData?.bankBranchDTO?.descriptionInfo?.localName}" type="Long" label="${message(code:'employeeSalaryInfo.transientData.bankBranchDTO.descriptionInfo.localName.label',default:'bankBranchId')}" />
    <lay:showElement value="${employeeSalaryInfo?.internationalAccountNumber}" type="String" label="${message(code:'employeeSalaryInfo.internationalAccountNumber.label',default:'internationalAccountNumber')}" />
    <lay:showElement value="${employeeSalaryInfo?.bankAccountNumber}" type="String" label="${message(code:'employeeSalaryInfo.bankAccountNumber.label',default:'bankAccountNumber')}" />
    <lay:showElement value="${employeeSalaryInfo?.salaryClassification}" type="enum" label="${message(code:'employeeSalaryInfo.salaryClassification.label',default:'salaryClassification')}" messagePrefix="EnumSalaryClassification" />
    <lay:showElement value="${employeeSalaryInfo?.salary}" type="double" label="${message(code:'employeeSalaryInfo.salary.label',default:'salary')}" />
    <lay:showElement value="${employeeSalaryInfo?.transientData.currencyDTO?.descriptionInfo?.localName}" type="Long" label="${message(code:'employeeSalaryInfo.transientData.currencyDTO.descriptionInfo.localName.label',default:'salaryCurrencyId')}" />
    <lay:showElement value="${employeeSalaryInfo?.salaryDate}" type="ZonedDate" label="${message(code:'employeeSalaryInfo.salaryDate.label',default:'salaryDate')}" />
    <lay:showElement value="${employeeSalaryInfo?.active}" type="Boolean" label="${message(code:'employeeSalaryInfo.active.label',default:'active')}" />
</lay:showWidget>
<el:row />
<el:row />
