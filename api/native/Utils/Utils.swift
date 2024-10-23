import Foundation

@objc public class Utils : NSObject {
    @objc public class func formatNumber(code:String,amount:Double) -> String {
        if #available(iOS 15.0, *) {
            return amount.formatted(.currency(code: code))
        } else {
            let currencyFormatter = NumberFormatter()
            currencyFormatter.usesGroupingSeparator = true
            currencyFormatter.numberStyle = .currency
            // localize to your grouping and decimal separator
            currencyFormatter.locale = Locale.current

            // We'll force unwrap with the !, if you've got defined data you may need more error checking

            return currencyFormatter.string(from: NSNumber(value: amount))!
        }
    }
}