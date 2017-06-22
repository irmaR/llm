nth(0,[X|_],X).
nth(N,[_|T],R):- M is N-1,nth(M,T,R).

println(Term):-
   class('java.lang.System').out <- get(Out1), 
   Out1<- println(Term).


%Getting hashmap of counts of discrete variables
%----------------------------------------------------------------------------------
getDiscreteCounts(General,Grounded,Context,Parent,HashMap):-
  
   General=Grounded,
   Parent=..[Name|Args],
   append(Args,[Val],Args2),
   Parent2=..[Name|Args2],
   append(Context,[Parent2],L),
   l2t(L,Term),!,
   call(Term),
   java_object('java.lang.String',[Val],Key),
   HashMap <- get(Key) returns Count,
   Count1 is Count+1,
   java_object('java.lang.Integer',[Count1],Counter),
   HashMap<-put(Key,Counter),fail.
   
   
   getDiscreteValueDeterm(General,Grounded,Context,Parent,Val):-
   General=Grounded,
   Parent=..[Name|Args],
   append(Args,[Val],Args2),
   Parent2=..[Name|Args2],
   append(Context,[Parent2],L),
   l2t(L,Term),!,
   call(Term).
   

   %Getting values when there is only Selector. That means that all the literals are boolean.
   %the feature is then Count or Exists.
   
   getDiscreteCountsOnlySelector(General,Grounded,Context,Count):-
   General=Grounded,
   l2t(Context,Term),!,
   call(Term),
   Count<- incrementCount returns Int,
   fail.

  
%Getting values for continuous parents for non-deterministic features such as Min, Max and so on. All values are stored in an arrayList
%--------------------------------------------------------------------------------
getContinuousValue(General,Grounded,Context,Parent,ArrayList):-
   General=Grounded,
   Parent=..[Name|Args],
   append(Args,[Val],Args2),
   Parent2=..[Name|Args2],
   append(Context,[Parent2],L),
   l2t(L,Term),!,
   call(Term),
   java_object('java.lang.Double',[Val],Value), 
   ArrayList<-add(Value) returns Ok, 
   fail.
   
%----------------------------------------------------------------------------------   


l2t([H], H) :- !.
l2t([H|T], (H,S)) :- l2t(T,S).

