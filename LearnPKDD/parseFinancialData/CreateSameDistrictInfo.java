package parseFinancialData;

import hybrid.core.CartesianProduct;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

public class CreateSameDistrictInfo {

	public static void main(String[] args) throws IOException{
		String path_to_district_csv_file="/cw/dtailocal/irma/RDN_Structure_Learning/ParsingFinancialData/financial/district.asc";
		String output_path_to_district_same_region_file="/cw/dtailocal/irma/RDN_Structure_Learning/ParsingFinancialData/financial/district_in_same_region.pl";

		BufferedReader br = null;
		BufferedWriter bw = new BufferedWriter(new FileWriter(new File(output_path_to_district_same_region_file)));
		String line = "";
		String cvsSplitBy = ";";

		HashMap<String,List<String>> region_to_districts=new HashMap<String,List<String>>();

		try {
			br = new BufferedReader(new FileReader(path_to_district_csv_file));
			while ((line = br.readLine()) != null) {
				// use comma as separator
				String[] instance = line.split(cvsSplitBy);
				String district_name=instance[1];
				String region_name=instance[2].replace(" ","");
			//	List<String> l=new ArrayList<String>();
			//	l.add(district_name);
				if(region_to_districts.containsKey(region_name)){
					region_to_districts.get(region_name).add(district_name);
				}
				else{
					region_to_districts.put(region_name, new ArrayList<String>());
					region_to_districts.get(region_name).add(district_name);
				}
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (br != null) {
				try {
					br.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		//System.out.println(region_to_districts);
		CartesianProduct<String> cartProduct=new CartesianProduct<String>();
		
		
		HashMap<String,List<List<String>>> cartesian_product_per_region=new HashMap<String,List<List<String>>>();
		for(String region:region_to_districts.keySet()){
			List<List<String>> tmp1=new ArrayList<List<String>>();
			tmp1.add(region_to_districts.get(region));
			tmp1.add(region_to_districts.get(region));
			List<List<String>> tmp=cartProduct.cartesianProduct(tmp1);
			cartesian_product_per_region.put(region, tmp);
		}
		
		
		System.out.println(cartesian_product_per_region);
		String pred_name="in_same_region";
		
		for(String region:cartesian_product_per_region.keySet()){
			for(List<String> s:cartesian_product_per_region.get(region)){
				
				if(s.get(0).equals(s.get(1))){
					continue;
				}
				else{
					
					bw.append(pred_name+"("+s.get(0)+","+s.get(1)+").\n");
				}
			}
		}
		bw.close();


	}

}

