package com.lgcns.test.examples;

public class Solution {
	
    public int solution(String s)
    {
        int answer = 0;
        
        //TODO:구현
        
        return answer;
    }

    public int solution2(String s)
    {
        return 0;
    }
    
	
	public static void main(String[] args) {
		Solution s = new Solution();

		// 리턴값 설정
		int result;
		

		// 테스트 데이터
		String str = "We'll start with the Logitech G920, a PC- and Xbox One-compatible wheel with 900 degrees of rotation and force feedback".toLowerCase();
		String[] testData = {
				str
				};
		
		int lenTest = testData.length;
		
		// 테스트셋 수행 
		for (int i=0; i <lenTest; i++) {
			result = s.solution(testData[i]);
			System.out.println(String.format("Answer: %d", result));
		}
	}
}
