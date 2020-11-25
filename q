[1mdiff --git a/src/main/java/com/naturalmotion/csr_api/service/gift/GiftBuilder.java b/src/main/java/com/naturalmotion/csr_api/service/gift/GiftBuilder.java[m
[1mindex e189244..135fd50 100644[m
[1m--- a/src/main/java/com/naturalmotion/csr_api/service/gift/GiftBuilder.java[m
[1m+++ b/src/main/java/com/naturalmotion/csr_api/service/gift/GiftBuilder.java[m
[36m@@ -16,8 +16,8 @@[m [mpublic class GiftBuilder {[m
 		return build(id, 13, brand, element.getPartType(), color.getGrade(), amount.intValue());[m
 	}[m
 [m
[31m-	public JsonObjectBuilder buildEssence(String id) {[m
[31m-		return build(id, 10, "", 7, 0, 2000);[m
[32m+[m	[32mpublic JsonObjectBuilder buildEssence(String id, BigDecimal qty) {[m[41m[m
[32m+[m		[32mreturn build(id, 10, "", 7, 0, qty.intValue());[m[41m[m
 	}[m
 [m
 	public JsonObjectBuilder buildEliteToken(EliteToken token, BigDecimal amount) {[m
[1mdiff --git a/src/main/java/com/naturalmotion/csr_api/service/gift/GiftService.java b/src/main/java/com/naturalmotion/csr_api/service/gift/GiftService.java[m
[1mindex ea64bcc..8d2e9a4 100644[m
[1m--- a/src/main/java/com/naturalmotion/csr_api/service/gift/GiftService.java[m
[1m+++ b/src/main/java/com/naturalmotion/csr_api/service/gift/GiftService.java[m
[36m@@ -12,7 +12,7 @@[m [mimport com.naturalmotion.csr_api.service.io.NsbException;[m
 [m
 public interface GiftService {[m
 [m
[31m-	JsonObject addEssence() throws CarException, NsbException;[m
[32m+[m	[32mJsonObject addEssence(BigDecimal qty) throws CarException, NsbException;[m[41m[m
 [m
 	JsonObject addFusions(List<FusionParam> colors, List<String> brands) throws NsbException;[m
 [m
[1mdiff --git a/src/main/java/com/naturalmotion/csr_api/service/gift/GiftServiceFileImpl.java b/src/main/java/com/naturalmotion/csr_api/service/gift/GiftServiceFileImpl.java[m
[1mindex 90d1e67..dee789c 100644[m
[1m--- a/src/main/java/com/naturalmotion/csr_api/service/gift/GiftServiceFileImpl.java[m
[1m+++ b/src/main/java/com/naturalmotion/csr_api/service/gift/GiftServiceFileImpl.java[m
[36m@@ -37,8 +37,8 @@[m [mpublic class GiftServiceFileImpl implements GiftService {[m
 	}[m
 [m
 	@Override[m
[31m-	public JsonObject addEssence() throws NsbException {[m
[31m-		JsonObjectBuilder gift = builder.buildEssence("0_0");[m
[32m+[m	[32mpublic JsonObject addEssence(BigDecimal qty) throws NsbException {[m[41m[m
[32m+[m		[32mJsonObjectBuilder gift = builder.buildEssence("0_0", qty);[m[41m[m
 [m
 		File nsb = nsbReader.getNsbFile(path);[m
 		JsonObject nsbObject = jsonBuilder.readJsonObject(nsb);[m
