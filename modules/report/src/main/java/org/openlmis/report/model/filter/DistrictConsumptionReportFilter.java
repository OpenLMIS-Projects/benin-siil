/*
 * Copyright © 2013 John Snow, Inc. (JSI). All Rights Reserved.
 *
 * The U.S. Agency for International Development (USAID) funded this section of the application development under the terms of the USAID | DELIVER PROJECT contract no. GPO-I-00-06-00007-00.
 */

package org.openlmis.report.model.filter;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.openlmis.report.model.ReportData;

import java.text.DateFormat;
import java.util.Date;

/**
 * User: Wolde
 * Date: 5/24/13
 * Time: 4:03 PM
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class DistrictConsumptionReportFilter implements ReportData {


    private String periodType;
    private int yearFrom;
    private int yearTo;
    private int monthFrom;
    private int monthTo;
    private Date startDate;
    private Date endDate;
    private int quarterFrom;
    private int quarterTo;
    private int semiAnnualFrom;
    private int semiAnnualTo;

    private int zoneId;
    private int productId;
    private int productCategoryId;
    private int rgroupId;
    private String rgroup;

    private String zone;
    private String product;
    private String productCategory;

    private int programId;


    @Override
    public String toString(){

        DateFormat dateFormatter = DateFormat.getDateInstance(DateFormat.DEFAULT);
        String periodFilterLabel =  "Period : ";
        String zoneFilterLabel   =  "Zone : ";
        String productCategoryFilterLabel =  "Product Category : ";
        String productFilterLabel =  "Product : ";
        String rggroupFilterLabel =  "Requisition Group : ";


        StringBuilder filtersValue = new StringBuilder("");
        filtersValue.append(String.format("%"+42+"s", periodFilterLabel)).append(dateFormatter.format(this.getStartDate())).append("-").append(dateFormatter.format(this.getEndDate())).append("\n").
                append(String.format("%"+35+"s", zoneFilterLabel)).append(this.getZone()).append("\n").
                append(String.format("%"+29+"s", productCategoryFilterLabel)).append(this.getProductCategory()).append("\n").
                append(String.format("%"+19+"s", productFilterLabel)).append(this.getProduct()).append("\n").
                append(String.format("%"+29+"s", rggroupFilterLabel)).append(this.getRgroup());

        return filtersValue.toString();
    }

}