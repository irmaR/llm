package parseFinancialData;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Create_Shares_Account {
	public static void main(String[] args) throws IOException{
		String path_to_district_csv_file="/cw/dtailocal/irma/RDN_Structure_Learning/ParsingFinancialData/financial/disp.asc";
		String output_path_to_district_share_account_file="/cw/dtailocal/irma/RDN_Structure_Learning/ParsingFinancialData/financial/share_account.pl";

		BufferedReader br = null;
		BufferedWriter bw = new BufferedWriter(new FileWriter(new File(output_path_to_district_share_account_file)));
		String line = "";
		String cvsSplitBy = ";";

		HashMap<String,List<String>> account_client_mapping=new HashMap<String,List<String>>();

		try {
			br = new BufferedReader(new FileReader(path_to_district_csv_file));
			while ((line = br.readLine()) != null) {
				// use comma as separator
				String[] instance = line.split(cvsSplitBy);
				String client_id=instance[1];
				String account_id=instance[2].replace(" ","");
				//	List<String> l=new ArrayList<String>();
				//	l.add(district_name);
				if(account_client_mapping.containsKey(account_id)){
					account_client_mapping.get(account_id).add("c_"+client_id);
				}
				else{
					account_client_mapping.put(account_id, new ArrayList<String>());
					account_client_mapping.get(account_id).add("c_"+client_id);
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
		int nr_accounts_more_than_one_client=0;
		int nr_accounts_more_than_two_client=0;
		for(String s:account_client_mapping.keySet()){
			if(account_client_mapping.get(s).size()>1){
				nr_accounts_more_than_one_client++;
			}
			if(account_client_mapping.get(s).size()>2){
				nr_accounts_more_than_two_client++;
			}
		}
		System.out.println(" Number of accounts having more than one client: "+nr_accounts_more_than_one_client);
		System.out.println(" Number of accounts having more than two clients: "+nr_accounts_more_than_two_client);
        
		//At most two clients share an account
		//896 accounts shared by two clients
		String pred_name="share_account";
		for(String s:account_client_mapping.keySet()){
			if(account_client_mapping.get(s).size()==2){
				bw.append(pred_name+"("+account_client_mapping.get(s).get(0)+","+account_client_mapping.get(s).get(1)+").\n");
				bw.append(pred_name+"("+account_client_mapping.get(s).get(1)+","+account_client_mapping.get(s).get(0)+").\n");
			}
		}
		bw.close();

	}

}	


