#if canImport(Testing)
import Testing
import IntelMklSrc

@Suite("IntelMklSrc Swift Export Tests")
struct IntelMklSrcExportTests {
    @Test("IntelMklSrc swift module imported cleanly")
    func testSwiftModuleLoads() throws {
        #expect(Bool(true), "IntelMklSrc swift module imported cleanly")
    }
}
#elseif canImport(XCTest)
import XCTest
import IntelMklSrc

final class IntelMklSrcExportTests: XCTestCase {
    func testSwiftModuleLoads() throws {
        XCTAssertTrue(true, "IntelMklSrc swift module imported cleanly")
    }
}
#endif
